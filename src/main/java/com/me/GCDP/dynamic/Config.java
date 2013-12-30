package com.me.GCDP.dynamic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.json.JSONArray;
import com.me.json.JSONException;

public class Config {
	private static Log log = LogFactory.getLog(Config.class);
	private static  Map<Integer,ConfigModel> cfg;
	private static  Map<Integer,ListModel> lcfg;
	private static Config instance = null;
	private Config()
	{
		loadConfig();
		loadListConfig();
	}
	public ConfigModel getConfig(Integer formId)
	{
		return cfg.get(formId);
	}
	public ListModel getListConfig(Integer listId)
	{
		return lcfg.get(listId);
	}
	public static Config GetInstance()
	{
		if(instance==null)
		{
			instance = new Config();
		}
		return instance;
	}
	public void loadConfig()
	{
		cfg = new HashMap<Integer,ConfigModel>();
		String json = readConfig("editor.json");
		try {
				JSONArray js = new JSONArray(json);
				for(int i=0;i<js.length();i++)
				{
					ConfigModel cm = new ConfigModel(js.getJSONObject(i));
					cfg.put(cm.getId(), cm);					
				}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error("json 配置解析错误editor.json:"+e.toString());
		}
	}
	public void loadListConfig()
	{
		lcfg = new HashMap<Integer,ListModel>();
		String json = readConfig("list.json");
		try {
				JSONArray js = new JSONArray(json);
				for(int i=0;i<js.length();i++)
				{
					ListModel lm = new ListModel(js.getJSONObject(i));
					lcfg.put(lm.getListId(), lm);					
				}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			log.error("json 配置解析错误list.json:"+e.toString());
		}
	}
	private String readConfig(String configName)
	{
		ClassLoader classloader = Config.class.getClassLoader();
		URL url = classloader.getResource("config/"+configName);		
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		 try
		 {
			 String path = URLDecoder.decode(url.getPath(),"utf-8");
			 File f = new File(path);
			 reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			 String text = null;
			 while ((text = reader.readLine()) != null)
			 {
			 	contents.append(text).append(System.getProperty("line.separator"));
			 }
		 } 
		 catch (Exception e) {
			
			log.error("读取配置文件出现错误:"+url.toString()+e.toString());
			return "";
		}
		 finally
		 {
			 if(reader!=null)
			 {
				 try {
					reader.close();
				} catch (IOException e) {
					log.error("关闭文件读取reader出现错误:"+url.toString()+e.toString());
				}
			 }
		 }
		 return contents.toString();
	}
}
