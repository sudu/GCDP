package com.me.GCDP.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.me.GCDP.util.SpringContextUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class IncludeDirective implements TemplateDirectiveModel{
	private static Log log = LogFactory.getLog(IncludeDirective.class);
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		JedisPool pool = (JedisPool)SpringContextUtil.getBean("jedisPool");
		Jedis redis = null;
		try{
			redis = pool.getResource();
			String url = params.get("url").toString();
			int cacheTime = Integer.parseInt(params.get("cacheTime").toString());
			
			String result = null;
			
			if(redis.exists(url)){
				result = redis.get(url);
			}else{
				result = sendGet(url);
				redis.set(url, result.toString());
				if(cacheTime>0){
					redis.expire(url, cacheTime);
				}
				log.info("set key:" + url + "| cache time:"+cacheTime);
			}
			
			Writer out = env.getOut();
			out.write(result);
	        if (body == null) {
	        	body = new TemplateDirectiveBody() {
					@Override
					public void render(Writer arg0) throws TemplateException, IOException {}
				};
	        }
	        body.render(env.getOut());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(redis != null){
				pool.returnResource(redis);
			}
		}
	}
	
	private String sendGet(String url) throws IOException{
		InputStreamReader br = null;
		try
		{
			String urlName = url;
			URL realUrl = new URL(urlName);
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();

			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);

			conn.setRequestMethod("GET");
			conn.connect(); 
						
			br = new InputStreamReader(conn.getInputStream(), "UTF-8");
			StringBuffer sb = new StringBuffer();
			int ch = 0;
			while ((ch = br.read()) != -1) {
				sb.append((char) ch);
			}
			
			return sb.toString();
			
		}catch(IOException e){
			log.error("HTTPsendGet:"+e.getMessage() + "|" + url);
			throw e;
		}finally{
			if(br != null){
				br.close();
			}
		}
	}

}
