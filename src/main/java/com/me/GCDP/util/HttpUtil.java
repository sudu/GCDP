package com.me.GCDP.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-17              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class HttpUtil {
	
	/*public static void main(String[] args) throws Exception{
		Map<String, String> m = new HashMap<String, String>();
		m.put("test", "好好好好好");
		//Map ret = HttpUtil.post("http://192.168.17.201/Cmpp/runtime/interface_53.jhtml", m , "UTF-8");
		Map ret = HttpUtil.get("http://192.168.17.201/Cmpp/runtime/interface_53.jhtml", "test="+URLEncoder.encode("好好好好好2","UTF-8") , "UTF-8");
		log.info(ret.get("content"));
		System.out.println(httpp.sendPost("http://v.cmpp.ifeng.com/Cmpp/runtime/interface_59.jhtml", 
				"a<?xml version=\"1.0\" encoding=\"utf-8\"?><CmsDataItems xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://tempuri.org/RmsToCmsInfo.xsd\"><Item><Id>a61cbf30-8b6f-4d8b-aecd-0be238a60079</Id><Name>香港股市继续创新高</Name><ObjectType>1</ObjectType><SPId>d5f1032b-fe8b-4fbf-ab6b-601caa9480eb</SPId><CategoryId>0019-0062-0005</CategoryId><CategoryName>财经</CategoryName><SitePathName>f</SitePathName><CreateDate>2007-1-3 14:10:10</CreateDate><KeyWord>香港股市,新高</KeyWord><TimeStamp>633034302217223750</TimeStamp><Status>1</Status><VideoPlayUrl>http://video.ifeng.com/video01/2007  '983090-d4ce-4b10-943d-d1c18bff77d4.flv</VideoPlayUrl><VideoDownUrl /><VideoPreviewUrl /><BigPosterUrl /><SmallPosterUrl>http://img.ifeng.com/itvimg/2007/01/03/c8c8e554-cdbb-4a6d-8bf5-a7aeec7f8c81.jpg</SmallPosterUrl><EPOSIDPosterUrl /><RecommPosterUrl /><CPId>d5f1032b-fe8b-4fbf-ab6b-601caa9480eb</CPId><ProgramType>0</ProgramType><TeleplayId /><TeleplayIndex>0</TeleplayIndex><ChannelId /><EditorId /><Abstract>香港股市继续创新高</Abstract><Description>6aaZ5riv6IKh5biC57un57ut5Yib5paw6auY</Description><Duration>66</Duration><PublishYear>2007</PublishYear><RecommLevel>0</RecommLevel><Director /><Actor /><Author /><Url>/f/200701/a61cbf30-8b6f-4d8b-aecd-0be238a60079.shtml</Url><DisPlay>1</DisPlay><publishstatus>1</publishstatus><syncDataTime>2008-6-3 18:54:20</syncDataTime><Column_ID>0</Column_ID><ColumnName /><SE_ID>0</SE_ID><SE_Title /><SE_Version>0</SE_Version><SE_KeyWord /><SE_Author /><CId /><ViewUrl /><publishdate /><WapStatus>0</WapStatus><IsSplit>0</IsSplit><MovieType /><EpisodeNum>0</EpisodeNum><TelDescripton /><reservefield2 /><SplitNum>0</SplitNum></Item></CmsDataItems>",
				null, "text/xml", null, 30000, 30000));
		
	}*/
	
	private static Log log = LogFactory.getLog(HttpUtil.class);
	
	/**
	 * 发送POST请求
	 * @param url 请求的路径
	 * @param dataMap 参数Map
	 * @return 返回请求结果Map  Map.get("content"):返回内容    Map.get("status_code"):请求状态代码
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> post(String url,  Map dataMap) throws Exception{
		return post(url, dataMap, "UTF-8");
	}
	
	/**
	 * 发送POST请求(UTF-8)
	 * @param url 请求的路径
	 * @param dataMap 参数Map
	 * @param charset 字符集
	 * @return 返回请求结果Map  Map.get("content"):返回内容    Map.get("status_code"):请求状态代码
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> post(String url,  Map dataMap, String charset) throws Exception{
		
		Map<String, Object> retMap = new HashMap<String, Object>();
		
		NameValuePair[] parametersBody = new NameValuePair[dataMap.keySet().size()];
		if(url == null || url.equals("") || dataMap == null || dataMap.keySet().size()==0){
			return null;
		}else{
			Iterator iter = dataMap.keySet().iterator();
			int i = 0;
			while(iter.hasNext()){
				Object key = iter.next();
				Object value = dataMap.get(key);
				parametersBody[i] = new NameValuePair(key.toString(), value == null ? "" : value.toString());
				i++;
			}
		}
		
		HttpClient client = new HttpClient();
		HttpClientParams pp = client.getParams();
		pp.setContentCharset(charset);
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Connection", "close");
		method.setRequestBody(parametersBody);
		StringBuffer html=new StringBuffer(); 
		InputStream in = null;
		Integer status_code = null;
		try{
			client.executeMethod(method);
			status_code = method.getStatusCode();
			in = method.getResponseBodyAsStream();
			
 			/*Scanner scanner=new Scanner(in,charset);
			while(scanner.hasNextLine()) {
				html.append(scanner.nextLine());
				html.append("\n");
			}*/
			
			// by HAN XIANQI, need perfecting
			BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
			int ch = 0;
			while ((ch = br.read()) != -1) {
				html.append((char) ch);
			}
			
		}catch(Exception e){
			log.error("post : " + url + "|" + e.getMessage());
			throw e;
		}finally{
			try{
				in.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			method.releaseConnection();
		}
		retMap.put("content", html.toString());
		retMap.put("status_code", status_code);
		return retMap;
	}
	
	/**
	 * 发送get请求(UTF-8)
	 * @param url
	 * @param queryString
	 * @return 返回请求结果Map  Map.get("content"):返回内容    Map.get("status_code"):请求状态代码
	 * @throws Exception 
	 */
	public static Map<String, Object> get(String url, String queryString) throws Exception{
		return get(url, queryString, "UTF-8");
	}
	
	/**
	 * 发送get请求
	 * @param url
	 * @param queryString
	 * @param charset 字符集
	 * @return 返回请求结果Map  Map.get("content"):返回内容    Map.get("status_code"):请求状态代码
	 * @throws Exception
	 */
	public static Map<String, Object> get(String url, String queryString, String charset) throws Exception{
		Map<String, Object> retMap = new HashMap<String, Object>();
		
	    HttpClient client = new HttpClient();
	    HttpMethod method = null;
	    HttpClientParams pp = client.getParams();
		pp.setContentCharset(charset);
	    StringBuffer html=new StringBuffer(); 
		InputStream in = null;
		Integer status_code = null;
	    try {
	    	if (queryString != null && !queryString.equals("")){
	    		if(url.indexOf("?")>0){
	    			url += "&"+queryString;
	    		}else{
	    			url += "?"+queryString;
	    		}
	        }
	    	method = new GetMethod(url);
	        client.executeMethod(method);
	        status_code = method.getStatusCode();
	        //response = method.getResponseBodyAsString();
	        in = method.getResponseBodyAsStream();
	        
	        /*Scanner scanner=new Scanner(in,charset);
			while(scanner.hasNextLine()) {
				html.append(scanner.nextLine());
				html.append("\n");
			}*/
			
			// by HAN XIANQI, need perfecting
			BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
			int ch = 0;
			while ((ch = br.read()) != -1) {
				html.append((char) ch);
			}

	    } catch(Exception e){
	    	log.error("get : " + url + "|" + e.getMessage());
	    	html.append(e.getMessage());
	    	throw e;
		} finally {
			try{
				in.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			method.releaseConnection();
	    }
		retMap.put("content", html.toString());
		retMap.put("status_code", status_code);
	    return retMap;
	}
	
	
	/**
	 * 
	 * @Description:文件上传
	 * @param @param reqEntity
	 * @param @param url
	 * @param @return
	 * @param @throws Exception   
	 * @return String
	 * @throws
	 */
	public static String uploadFile(MultipartEntity reqEntity,String url) throws Exception{
			DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            Scanner scanner = null;
            StringBuffer str = null;
            try{
	            httppost.setEntity(reqEntity);
	
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity resEntity = response.getEntity();

	            scanner=new Scanner(resEntity.getContent());
				str=new StringBuffer();
				while(scanner.hasNextLine()) {
				        str.append(scanner.nextLine());
				        str.append("\n");
				}
	           
	            EntityUtils.consume(resEntity);
            }catch(Exception e){
            	log.error("uploadFile : " + url + "|" + e.getMessage());
            	throw e;
            }finally{
	            scanner.close();
	            httpclient.getConnectionManager().shutdown();
            }
            return str.toString();
	}
	
	/**
	 * 下载文件
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBinaryFile(String url) throws IOException{
		InputStream raw = null;
		InputStream in = null;
		try{
			URL u = new URL(url);
			URLConnection uc = u.openConnection();
			//String contentType = uc.getContentType();
			int contentLength = uc.getContentLength();
			raw = uc.getInputStream();
			in = new BufferedInputStream(raw);
			byte[] data = new byte[contentLength];
			int bytesRead = 0;
			int offset = 0;
			while (offset < contentLength){
				bytesRead = in.read(data,offset,data.length-offset);
				if(bytesRead == -1) break;
				offset +=bytesRead;
			}
	
			if (offset != contentLength){
				log.error("getBinaryFile-->Only read "+offset+" bytes;Expected "+contentLength+" bytes.");
				return null;
			}
			return data;

		}catch(IOException e){
			log.error("getBinaryFile : " + url + "|" + e.getMessage());
			throw e;
		}finally{
			if(raw!=null){
				raw.close();
			}
			if(in!=null){
				in.close();
			}
		}
	}
	
}
