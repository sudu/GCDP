package com.me.GCDP.xform;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.me.GCDP.util.MySQLHelper;
import com.me.json.JSONException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TableSchemaHelper {
	public static void createTable(FormConfig conf) throws IOException, JSONException, SQLException, TemplateException
	{
		Vector<ControlDbItem> fields = conf.getDbItem();
		Template t = getSqlTemplate("createsql.ftl");
		Map<String, Object> map = new HashMap<String, Object>();
		String tableName = "frm_"+conf.getFormId();
		map.put("tableName", tableName);
		map.put("fields", fields);
		Writer out = new StringWriter();
		t.process(map, out);
		String sql = out.toString();
		//System.out.println(sql);
		MySQLHelper.ExecuteNoQuery(sql, null);
		sql ="update cmpp_formConfig set tableName='"+tableName+"' where formId="+conf.getFormId();
		MySQLHelper.ExecuteNoQuery(sql, null);
		conf.setTableName(tableName);
		//创建备份表
		if(conf.hasPartition())
		{
			createBakTable(conf);
		}
		
	}
	private static void createBakTable(FormConfig conf) throws SQLException, IOException, TemplateException, JSONException
	{
		Vector<ControlDbItem> fields = conf.getDbItem();
		Template t = getSqlTemplate("createsql_bak.ftl");
		Map<String, Object> map = new HashMap<String, Object>();
		String tableName = conf.getTableName()+"_bak";
		map.put("tableName", tableName);
		map.put("fields", fields);
		Writer out = new StringWriter();
		t.process(map, out);
		String sql = out.toString();
		MySQLHelper.ExecuteNoQuery(sql, null);
		//创建备份表
	}
	public static void updateTable(FormConfig conf) throws IOException, JSONException, SQLException, TemplateException
	{
		Vector<ControlDbItem> fields = conf.getDbItem();
		Template t = getSqlTemplate("updatesql.ftl");
		Map<String, Object> map = new HashMap<String, Object>();
		String tableName = conf.getTableName();
		Map<String,Map<String,Object>> tableSchema = getTableSchema(tableName);
		map.put("tableName", tableName);
		Vector<String> data = new Vector<String>();
		for(ControlDbItem field:fields)
		{
			String fieldType = getFieldDefine(field);
			String fieldName = field.getFieldName();
			if(!fieldName.equals("id"))
			{
				if(tableSchema.containsKey(fieldName))
				{
					if(!fieldType.equals(tableSchema.get(fieldName).get("Type")))
					{
						data.add("modify `"+fieldName+"` "+fieldType);
					}
				}
				else
				{
					data.add("add `"+fieldName+"` "+fieldType);
				}
			}
		}
		map.put("data", data);
		Writer out = new StringWriter();
		t.process(map, out);
		String sql = out.toString();
		//System.out.println(sql);
		MySQLHelper.ExecuteNoQuery(sql, null);
		sql ="update cmpp_formConfig set tableName='"+tableName+"' where formId="+conf.getFormId();
		MySQLHelper.ExecuteNoQuery(sql, null);	
		if(conf.hasPartition())
		{
			if(existsTable(tableName+"_bak"))
			{
				out = new StringWriter();
				t = getSqlTemplate("updatesql_bak.ftl");
				map.put("tableName", tableName+"_bak");
				t.process(map, out);
				sql = out.toString();
				//System.out.println(sql);
				MySQLHelper.ExecuteNoQuery(sql, null);
			}
			else
			{
				createBakTable(conf);
			}
		}
	}
	private static boolean existsTable(String tableName) throws SQLException
	{
		String sql = "select   1   from   information_schema.tables   where   table_name   =   '"+tableName+"'";
		if(MySQLHelper.ExecuteSql(sql, null).size()>0)
		{
			return true;
		}
		return false;
	}
	private static String getFieldDefine(ControlDbItem field)
	{
		String type = field.getFieldType().toUpperCase();
		fieldType ft = fieldType.valueOf(type);
		switch(ft){
			case CHAR:
			case VARCHAR:
			case INT:
				return type.toLowerCase()+"("+field.getFieldLength()+")";
			default:
				return type.toLowerCase();
		}
			
	}
	private static Template getSqlTemplate(String fileName) throws IOException
	{
		Configuration config = new Configuration(); 
		ClassLoader classloader = TableSchemaHelper.class.getClassLoader();
		URL url = classloader.getResource("config/");
		String path = URLDecoder.decode(url.getPath(),"utf-8");
		config.setDirectoryForTemplateLoading(new File(path));   
		config.setObjectWrapper(new DefaultObjectWrapper());
		Template template = config.getTemplate(fileName,"UTF-8"); 
		return template;
	}
	private static Map<String,Map<String,Object>> getTableSchema(String tableName) throws SQLException
	{
		Map<String,Map<String,Object>> rtn = new HashMap<String,Map<String,Object>>();
		Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql("desc "+tableName, null);
		for(Map<String,Object> mp:data)
		{
			rtn.put(mp.get("Field").toString(), mp);
		}
		return rtn;
	}

}
