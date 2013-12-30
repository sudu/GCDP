package com.me.GCDP.dynamic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.MySQLHelper;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class ListUtil {
	private static Log log = LogFactory.getLog(ListUtil.class);
	int start;
	int end;
	List<String> fiels;
	List<String> searchFields;
	String table;
	String strOrder="";
	String strWhere="";
	String customSql ="";
	String customFilter="";
	Object[] parms=null;
	int listId;
	Map<String, String[]> form;
	public  ListUtil(int listId,Map<String, String[]> form2) throws JSONException, SQLException
	{
		this.form = form2;
		ListModel conf = Config.GetInstance().getListConfig(listId);
		customSql = conf.getSql();
		customFilter = formatFilter(conf.getFilter());
		fiels = conf.getListFields();
		if(!fiels.contains("id"))
		{
			fiels.add("id");
		}
		searchFields = conf.getSearchFields();	
		if(!searchFields.contains("id"))
		{
			searchFields.add("id");
		}
		this.listId = listId;
		table = conf.getTableName();

	}
	private String formatFilter(String filter)
	{
		//针对form中的filter参数符号替换为参数内容
		if(form==null)
		{
			return filter;
		}
		Pattern tagP =  Pattern.compile("(?<=(\\{))[\\s\\S]*?(?=\\})");
		Matcher tagM = tagP.matcher(filter);
		while(tagM.find())
		{
			String tag = tagM.group(0).trim();
			Object[] vs = form.get(tag);
			String value="";
			if(vs!=null)
			{	if(vs.length>0)
				{
					value = vs[0].toString();
				}
			}			
			filter = filter.replaceAll("\\{"+tag+"\\}", value);
		}
		return filter;
	}
	public int getTotalNum()
	{
		String sql;
		//customSql不支持order by 和 limit
		String mySql =customSql;
		if(mySql==null || mySql.equals("")){
			String w = strWhere;
			if(!w.equals("")) w=" and " + w; 
			sql="select count(*) num from "+table+" where 1=1 "+w;
		}else{
			int pos = mySql.indexOf("where");
			String w = strWhere;
			if(strWhere.equals("")) w = " where 1=1 ";
			else w=" where " + strWhere;
			if(pos!=-1){
				mySql = mySql.substring(0,pos) + w + " and " + mySql.substring(pos + 6 );
			}else{
				mySql += " " + w;
			}
			sql = "select count(*) num "  + mySql.substring(mySql.indexOf("from"));
		}
			
		try {
			//System.out.println(sql);
			return((Long)(MySQLHelper.ExecuteSql(sql, parms).get(0).get("num"))).intValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public Vector<Map<String,Object>> getData() throws SQLException, JSONException
	{
		String sql;
		String mySql =customSql;
		String tb = table;
		if(customSql==null || customSql.equals("")){
			if(!strWhere.equals(""))
			{
				strWhere += " and ";	
			}
			sql="select "+getFields()+" from "+tb+ " where " + strWhere+"  1=1 "+strOrder+" limit "+start+","+end;
		}else{
			int pos = mySql.indexOf("where");
			if(pos!=-1){
				String w = strWhere;
				if(strWhere.equals("")) w = " where 1=1 ";
				else w=" where " + strWhere;	
				mySql = mySql.substring(0,pos) + w + " and " + mySql.substring(pos + 5) +" "+strOrder+" limit "+start+","+end;
			}else{
				String w = strWhere;
				if(strWhere.equals("")) w = " where 1=1 ";
				else w = " where " + w + " ";
					
				mySql = mySql + w +strOrder+" limit "+start+","+end;
			}
			log.info("mySql_util: "+mySql);
			sql = mySql;
		}
		log.info("parms_util"+parms);
		//System.out.print(sql);
		return MySQLHelper.ExecuteSql(sql, parms);
	}

	private String getFields() throws JSONException
	{	
		String rtn="*";
		if(fiels.size()>0){
			for(int i=0;i<fiels.size();i++){
				if(i==0)
				{
					rtn = "`"+fiels.get(i)+"`";
				}
				else
				{
					rtn = rtn+",`"+ fiels.get(i)+"`";
				}
			}
		}
		return rtn;
	}
	
	public void initParms(String wheres,String orders,int start,int end) throws Exception
	{
		if(orders!=null){
		initOrder(new JSONArray(orders));
		}
		if(wheres!=null)
		{
			initWhere(new JSONArray(wheres));
		}
		if(!customFilter.equals("") && customFilter!=null){
			if(strWhere.equals("")){
				strWhere =" (" + customFilter + ")" ;
			}else{
				strWhere =" " + strWhere + " and (" + customFilter + ")" ;
			}			
		}

	
		this.start = start;
		this.end = end;
	}
	private void initOrder(JSONArray orders) throws JSONException
	{
		strOrder="";
		if(orders==null)
			return;
		int len = orders.length();
		if(len==0)
		{
			return;
		}
		for(int i=0;i<len;i++)
		{
			JSONObject order = orders.getJSONObject(i);
			String field = order.getString("field");
			String type = order.getString("order");
			field = field.replaceAll("`", "");
			field = "`"+field+"`";
			if(!type.equals("desc"))
			{
				type = "asc";
			}
			if(strOrder.equals(""))
			{
				strOrder = " order by "+field+" "+type;
			}
			else
			{
				strOrder = strOrder+","+field+" "+type;
			}
		}
	}
	private  void initWhere(JSONArray wheres) throws Exception
	{
		strWhere="";
		if(wheres==null)
			return;
		//parms = new Object[wheres.length()];
		List<Object[]> andCondition = new ArrayList<Object[]>();
		List<Object[]> orCondition = new ArrayList<Object[]>();
		for(int i=0;i<wheres.length();i++)
		{
			
			JSONObject where = wheres.getJSONObject(i);
			String field = where.getString("field");
			String op = where.getString("op");
			String value = where.getString("value");
			String with = where.getString("andor").equals("or")?"or":"and";
			List<Object[]> currentCondition = with.equals("and")?andCondition:orCondition;
			Object[] conditionParam;

			if(customSql!=null || !customSql.equals("") || searchFields.contains(field))
			{
				//初始化条件
				field = '`'+field+'`';
				String sw="";
				if(op.equals("="))
				{
					sw = field+"=?";
				}
				else if(op.equals("like"))
				{	//如果like查询参数中用空格间隔的话会做and查询
					sw = field+" like ?";
				}
				else if(op.equals(">"))
				{
					sw = field+">?";
				}
				else if(op.equals(">="))
				{
					sw = field+">=?";
				}
				else if(op.equals("<"))
				{
					sw = field+"<?";
				}
				else if(op.equals("<="))
				{
					sw = field+"<=?";
				}
				else if(op.equals("<>"))
				{
					sw = field+"<>?";
				}
				else
				{
					sw = field+"=?";
				}

				//如果是like操作需要根据value按空格特殊处理为多条件筛选	
				if(op.equals("like"))
				{
					//为like查询的时候需要按照参数情况做参数切分加入
					String[] values = value.trim().replaceAll("[ ]+", " ").split(" ");
					conditionParam = new Object[values.length+1];
					conditionParam[0] = sw;
					int k =1;
					//System.out.println(values.length);
					for(Object pa :values)
					{
						conditionParam[k] = "%"+pa+"%";
						//System.out.println(conditionParam[k]);
						k++;
					}
				}
				else
				{
					conditionParam = new Object[2];
					conditionParam[0] = sw;
					conditionParam[1] = value;					
				}
				currentCondition.add(conditionParam);
			}
			else
			{
				throw new Exception("不正确的搜索字段'"+field+"'该字段未设置为可搜索");
			}
			
		}
		//初始化SQL
		List<Object> plst = new ArrayList<Object>();
		if(andCondition.size()>0)
		{
			strWhere = contractConditon(" and ", andCondition, plst);
		}
		if(orCondition.size()>0)
		{
			if(strWhere.equals(""))
			{
				strWhere = " "+contractConditon(" or ", orCondition, plst);
			}
				else
				{
				if(orCondition.size()>1)
				{
					strWhere = strWhere+" and ("+contractConditon(" or ", orCondition, plst)+")";
				}
				else
				{
					strWhere = strWhere+" or ("+contractConditon(" or ", orCondition, plst)+")";
				}
			}
		}
		//初始化加入的参数
		parms = new Object[plst.size()];
		int k=0;
		for(Object pa:plst)
		{
			parms[k] = pa;
			k++;
		}
	}
	public void delete(int id) throws SQLException
	{
		String sql ="delete from "+table+" where id="+id;
		MySQLHelper.ExecuteNoQuery(sql, null);
	}
	public static String contractConditon(String with,List<Object[]> conditions,List<Object> plst)
	{
		String strWhere="";
		for(Object[] parm:conditions)
		{
			if(!strWhere.equals(""))
			{
				strWhere = strWhere+with;
			}
			//当参数大于2的时候认为是多级筛选过滤
			if(parm.length>2)
			{
				strWhere=strWhere+"("+parm[0];
				plst.add(parm[1]);
				for(int i=2;i<parm.length;i++)
				{
					strWhere=strWhere+" and "+parm[0];
					plst.add(parm[i]);
				}
				strWhere = strWhere+")";
			}
			else
			{
				strWhere=strWhere+parm[0];
				plst.add(parm[1]);
			}
		}
		return strWhere;
	}
}
