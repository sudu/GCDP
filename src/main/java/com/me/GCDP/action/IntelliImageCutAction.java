package com.me.GCDP.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.util.HttpUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.json.JSONException;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class IntelliImageCutAction extends ActionSupport {
	
	private static Log log = LogFactory.getLog(IntelliImageCutAction.class);
	private String url = null;
	private String width = null;
	private String height = null;
	private String ext = null;
	
	private String image = null;
	private String domain = null;
	
	private Map<String, Object> coordinateRet = null;
	private String sendfileRet = null;
	private boolean hasError = false;
	private static String intelliImageCutUrl = null;
	private static String intelliSendFileUrl = null;
	
	static {
		String server = CmppConfig.getKey("image.URL");
		String context = CmppConfig.getKey("image.ServerName");
		intelliImageCutUrl = "http://" + server + "/" + context + "/coordinate";
		intelliSendFileUrl = "http://" + server + "/" + context + "/sendfile";
	}
	
	public void coordinate() {
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("url", url);
		params.put("width", width);
		params.put("height", height);
		log.info("width:" + width + ",height:" + height + ",ext:" + ext);
		if(url != null && url.startsWith("http://")) {
			log.info("url:" + url);
		}else {
			log.info("url:base64");
		}
		
		if(ext != null && !"".equals(ext)) {
			params.put("ext", ext);
		}
		try {
			coordinateRet = HttpUtil.post(intelliImageCutUrl, params);
			response.getWriter().write(coordinateRet.get("content").toString());
		} catch (Exception e) {
			log.error("IOException when request:" + intelliImageCutUrl, e);
			JSONObject errObj = new JSONObject();
			try {
				errObj.put("success", false);
				errObj.put("msg", "获取智能裁图坐标失败");
			} catch (JSONException e2) {
				log.error("Exception occured when assembling coordinate error info" + e2);
			}
			
			try {
				response.getWriter().write(errObj.toString());
			} catch (IOException e1) {
				log.error("Exception occured when sending coordinate error result to client",e1);
			}
		}
		
	}
	
	public void sendfile(){
		HttpServletResponse response =ServletActionContext.getResponse();
		response.setCharacterEncoding("UTF-8");
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("domain", domain);
		params.put("ext", ext);
		params.put("width", width);
		params.put("height", height);
		params.put("image", image);
		log.info("image:base64,domain:" + domain + ",ext:" + ext);
		try {
			Map<String, Object> sendfileRet = HttpUtil.post(intelliSendFileUrl, params);
			response.getWriter().write(sendfileRet.get("content").toString());
		}catch (Exception e) {
			log.error("IOException when request:" + intelliSendFileUrl, e);
			JSONObject errObj = new JSONObject();
			try {
				errObj.put("success", false);
				errObj.put("msg", "图片分发失败");
			} catch (JSONException e2) {
				log.error("Exception occured when assembling sendfile error info" + e2);
			}
			
			try {
				response.getWriter().write(errObj.toString());
			} catch (IOException e1) {
				log.error("Exception occured when sending sendfile error result to client",e1);
			}
		}
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public boolean isHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	
	
	
	public String getSendfileRet() {
		return sendfileRet;
	}
	public void setSendfileRet(String sendfileRet) {
		this.sendfileRet = sendfileRet;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
