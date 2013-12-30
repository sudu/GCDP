package com.me.GCDP.xform;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.me.GCDP.mapper.TemplateMapper;
import com.me.GCDP.model.Template;
import com.me.GCDP.util.MySQLHelper;
import com.me.GCDP.util.SpringContextUtil;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class BuildBlock {
	public static JSONObject build(String cmd,Template template) throws JSONException
	{
		JSONObject rtn = new JSONObject();
		try
		{
			 String content = template.getContent();
			 JSONArray ocmd = new JSONArray(cmd);
			 Pattern pt = Pattern.compile("<cmpp_banner>[\\s\\S]*?</cmpp_banner>");
			 Matcher mc = pt.matcher(content);
			 ArrayList<String> lst1 = new ArrayList<String>();
			 while(mc.find())
			{
				String block = mc.group();
				lst1.add(block);
			}
			mc = mc.reset();
			ArrayList<String> lst2 = new ArrayList<String>();
			int p = 0;
			String blocks="";
			 for(int i=0;i< ocmd.length();i++)
			 {
				 boolean isLast = (i==ocmd.length()-1);
				 JSONObject cd = (JSONObject) ocmd.get(i);
				 int cp = cd.getInt("pos");
				 String act ="";
				 if(!cd.isNull("action"))
				 {
					 act = cd.getString("action");
				 }
				 String block="";
				 if(act.equals("add"))
				 {
					 int bId = cd.getInt("bannerId");
					 block = addTag(getBlock(bId));
				 }
				 else if(act.equals("delete"))
				 {					 
				 }
				 else
				 {
					 int oid = cd.getInt("oldOrder");
					 block = lst1.get(oid);
				 }
				 if(cp!=p)
				 {
					 lst2.add(blocks);
					 p = cp;
					 blocks=block;
				 }
				 else
				 {
					 blocks = blocks+"\r\n"+block;
				 }
				 if(isLast)
				 {
					 lst2.add(blocks);
				 }
			 }
			 if(lst1.size()!=lst2.size())
			 {
				 rtn.put("success", false);
				 rtn.put("message", "位置不匹配");
			 }
			 else
			 {
//				 for(int k=0;k<lst1.size();k++)
//				 {
//					 content = content.replace(lst1.get(k), "mmmmm"+k);
//				 }
				 mc = mc.reset();
				 StringWriter sw = new StringWriter();
				 int st=0;
				 int j=0;
				 while(mc.find())
				 {
					 	int start = mc.start();
						int end = mc.end();
						if(start>st)
						{
							sw.append(content.substring(st, start));							
						}
						sw.append(lst2.get(j));
						st = end;
						j++;
				 }
				if(st<content.length())
				{
					sw.append(content.substring(st));
				}
				Template t = new Template();
				t.setId(template.getId());
				t.setContent(sw.toString());
				TemplateMapper<Template> templateMapper = (TemplateMapper<Template>) SpringContextUtil.getBean("templateMapper");
				templateMapper.update(t);
//				String sql = "update cmpp_template set content=? where id =?";
//				Object[] params= new Object[2];
//				params[0] = sw.toString();
//				params[1] = template.getId();
//				MySQLHelper.ExecuteNoQuery(sql, params);
				template.setContent(sw.toString());
				rtn.put("success", true);
				rtn.put("message", "处理成功");
			 }
		}
		catch (Exception e) {
			// TODO: handle exception
			rtn.put("success", false);
			rtn.put("message", e.getMessage());
			e.printStackTrace();
		}
		return rtn;
	}
	private static String getBlock(int key) throws Exception
	{
		String sql = "select content from cmpp_banner where id =?";
		Object[] params= new Object[1];
		params[0] = key;
		Vector<Map<String,Object>> data= MySQLHelper.ExecuteSql(sql, params);
		if(data.size()<1)
		{
			throw new Exception("未查到ID为:"+key+" 的通栏");
		}
		else
		{
			return data.get(0).get("content").toString();
		}
	}
	private static String addTag(String block)
	{
		return "<cmpp_banner>"+block+"</cmpp_banner>";
	}

}
