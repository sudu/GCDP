package com.me.GCDP.dynamic;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.util.MySQLHelper;

public class DataUtil {
	public static int saveData(Map<String, String[]> form,Integer formId,int id) throws Exception
	{
		int rtn=0;
		ConfigModel cm = Config.GetInstance().getConfig(formId);
		if(cm!=null)
		{
			String fields = cm.getInsertField();
			if(id!=0)
			{
				fields = cm.getUpdateField();
			}
			String table = cm.getTableName();
			if(fields.equals(""))
			{
				throw new Exception("未设置表单的保存字段"+formId);
			}
			String[] arr = fields.split(",");
			Map<String,Object> data = new HashMap<String, Object>();
			for(String f:arr)
			{

				if(!f.equals(""))
				{
					Object[] value = form.get(f);
					if(value!=null)
					{
						//System.out.println(key+":"+(values[0]));
						if(value[0].equals(""))
						{
							data.put(f,null);
						}
						else
						{
							data.put(f,value[0]);	
						}
					}
				}
			}
			//System.out.println(fields);
			//设置绑定了时间和日期的字段
			String timeFields = cm.getTimeField();
			arr = timeFields.split(",");
			for(String tf:arr)
			{
				if(!tf.equals(""))
				{
					if(cm.isContainField(tf, fields)&&(!data.keySet().contains(tf)))
					{
						Date d = new Date();
						data.put(tf, d);
					}
				}
			}
			//设置绑定了系统当前用户的字段
			String userFields = cm.getUserField();
			arr = userFields.split(",");
			for(String uf:arr)
			{
				if(uf.equals(""))
				{
					continue;
				}
				else
				{
					if(cm.isContainField(uf, fields)&&(!data.keySet().contains(uf)))
					{   
						//String userId = AuthorzationUtil.getUserId();
				    	String userName =  AuthorzationUtil.getUserName();
						data.put(uf,  userName);
					}
				}
			}
			//System.out.println(data);
			if(id!=0)
			{
				update(data, table,id);
				rtn = id;
			}
			else
			{
				rtn =  insert(data, table);
			}
		}
		else
		{
			throw new Exception("未能根据formId加载表单配置");
		}
		return rtn;
		
	}
	private static void update(Map<String, Object> fields,String table,int id) throws SQLException
	{
		Object[] parms;
		if(fields.isEmpty())
		{
			return;
		}
		else
		{
			parms = new Object[fields.size()];
		}
		String sql ="update %s set %s where id="+id;
		int k=0;
		String fs="";
		for(String key:fields.keySet())
		{
			if(fs.equals(""))
			{
				fs = "`"+key+"`=?";
			}
			else
			{
				fs = fs+",`"+key+"`=?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		String tb = table;
		sql = String.format(sql,tb,fs);
		
		MySQLHelper.ExecuteNoQuery(sql, parms);
	}
	private static int insert(Map<String, Object> fields,String table) throws SQLException
	{
		Object[] parms;
		if(fields.isEmpty())
		{
			return 0;
		}
		else
		{
			parms = new Object[fields.size()];
		}
		String sql="";

		sql ="insert into %s(%s) values(%s)";
		String fs="";
		String vs="";
		int k=0;
		for(String key:fields.keySet())
		{
			if(fs.equals(""))
			{
				fs = "`"+key+"`";
				vs = "?";
			}
			else
			{
				fs = fs+",`"+key+"`";
				vs = vs+","+"?";
			}
			parms[k] = fields.get(key);
			k++;
		}
		sql = String.format(sql, table,fs,vs);
		return Integer.parseInt(MySQLHelper.InsertAndReturnId(sql, parms));
	}
	public static Map<String,Object> getData(int formId,int id) throws SQLException
	{
		List<Map<String,Object>> rtn = null;
		ConfigModel cm = Config.GetInstance().getConfig(formId);
		String sql = "select "+cm.getReturnField()+" from "+cm.getTableName()+" where id="+id;
		rtn = MySQLHelper.ExecuteSql(sql, null);
		if(rtn.size()>0)
		{
			return rtn.get(0);
		}
		else
		{
			return null;
		}
		
	}
	
}
