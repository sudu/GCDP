package com.me.GCDP.util.image;

import java.util.Map;

import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.XMLAnalysis;


/**
 * <p>Title:封装接口</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-10-12              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class ImageUtil {
	
	/**
	 * 
	 * @Description:切割图片
	 * @param url 图片url
	 * @param height 切割后高度
	 * @param width 切割后宽度
	 * @param percent 切割位置，距离上方的高度
	 * @param isNonsync 是否异步
	 * @param callback 回调url
	 * @param channelID 分发id
	 * @param wmUrl 水印url
	 * @param position 水印位置
	 * @throws Exception 线程安全  
	 * @return Map<String,Object> 
	 */
	public static Map<String,Object> cutImage(String url, 
			String height,String width,String percent,String isNonsync,
			String callback,String channelID,String wmUrl,String position) throws Exception{
		if(percent.equals("")||percent == null){
			percent = "0";
		}
		if(position.equals("")||position == null){
			position = "0";
		}
		String queryString = "height="+height+"&"+"width="+width+"&"+"percent="+percent+"&"
				+"isNonsync="+isNonsync+"&"+"callback="+callback+"&"+"channelID="+channelID
				+"&"+"wmUrl="+wmUrl+"&"+"position="+position;
		Map<String,Object> returnMap = HttpUtil.get(url, queryString);
		String html = null;
		if((Integer)returnMap.get("status_code") == 200){
			return null;
		}
		else{
			html = (String) returnMap.get("content");
			
		}
		return XMLAnalysis.allAnalysis(html);
	}
	/**
	 * 
	 * @Description:调整图片大小
	 * @param url 图片url
	 * @param height 调整后高度
	 * @param width 调整后宽度
	 * @param percent 调整百分比
	 * @param isNonsync 是否异步
	 * @param callback 回调url
	 * @param channelID 分发id
	 * @param wmUrl 水印url
	 * @param position 水印位置
	 * @throws Exception 线程安全   
	 * @return Map<String,Object> 
	 * Object ob = map.get("子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的text值)<br>
	 * Object ob = map.get("属性名@子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的Attribute值)<br>
	 */
	public static Map<String, Object> adjustSize(String url,
			String height,String width,String percent,String isNonsync,
			String callback,String channelID,String wmUrl,String position) throws Exception{
		if(position == null||position.equals("")){
			position = "0";
		}
		if(height == null||height.equals("")){
			height = "0";
		}
		if(width == null||width.equals("")){
			width = "0";
		}
		if(percent == null||percent.equals("")){
			percent = "0";
		}
		String queryString = "height="+height+"&"+"width="+width+"&"+"percent="+percent+"&"
				+"isNonsync="+isNonsync+"&"+"callback="+callback+"&"+"channelID="+channelID
				+"&"+"wmUrl="+wmUrl+"&"+"position="+position;
		Map<String,Object> returnMap = HttpUtil.get(url, queryString);
		String html = null;
		if((Integer)returnMap.get("status_code") == 200){
			return null;
		}
		else{
			html = (String) returnMap.get("content");
			
		}
		return XMLAnalysis.allAnalysis(html);
	}
	/**
	 * 
	 * @Description:添加水印
	 * @param url 图片url
	 * @param isNonsync 是否异步
	 * @param callback 回调url
	 * @param channelID 分发id
	 * @param wmUrl 水印url
	 * @param position 水印位置
	 * @throws Exception 线程安全   
	 * @return Map<String,Object> 
	 * Object ob = map.get("子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的text值)<br>
	 * Object ob = map.get("属性名@子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的Attribute值)<br>
	 */
	public static Map<String, Object> addWatermark(String url, 
			String isNonsync,String callback,String channelID,
			String wmUrl,String position) throws Exception{
		
		String queryString = "isNonsync="+isNonsync+"&"+"callback="
			+callback+"&"+"channelID="+channelID+"&"+"wmUrl="+wmUrl
			+"&"+"position="+position;
		
		Map<String,Object> returnMap = HttpUtil.get(url, queryString);
		String html = null;
		if((Integer)returnMap.get("status_code") == 200){
			return null;
		}
		else{
			html = (String) returnMap.get("content");
			
		}
		return XMLAnalysis.allAnalysis(html);
	}
	/**
	 * 
	 * @Description:图片批处理
	 * @param url 图片url
	 * @param dataMap
	 * @throws Exception   
	 * @return Map<String,Object>:
	 * Object ob = map.get("子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的text值)<br>
	 * Object ob = map.get("属性名@子节点.父节点.")<br>
	 * if(这个节点的同名子节点不止一个，则ob为List类型,否则为直接为xml里面的Attribute值)<br>
	 */
	public static Map<String, Object> picturesBatch(String url,  Map<String, Object> dataMap) throws Exception{
		Map<String,Object> returnMap = HttpUtil.post(url, dataMap);
		String html = null;
		if((Integer)returnMap.get("status_code") == 200){
			return null;
		}
		else{
			html = (String) returnMap.get("content");
			
		}
		return XMLAnalysis.allAnalysis(html);
	}

}
