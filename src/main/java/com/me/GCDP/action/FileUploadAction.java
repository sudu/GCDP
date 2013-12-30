package com.me.GCDP.action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.dom4j.DocumentException;
import org.jsoup.helper.StringUtil;

import com.me.GCDP.util.ImageCompressUtil;
import com.me.GCDP.util.MD5;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.plugin.ImagePlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONArray;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2008-12-4               Hu WeiQi               create the class     </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class FileUploadAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	private static Log log = LogFactory.getLog(FileUploadAction.class);
	
	private File filedata;
	private String filedataFileName;
	//private String filedataContentType;
	
	private String msg = null;
	private long bytes;//文件大小
	
	private Boolean hasError = false;
	
	private String syncflag = null;
	
	private String extradata = null;
	private String domain = null;
	private String url = null;
	private String waterMarkUrl=null;
	private String position=null;
	
	private Integer width = null;
	private Integer height = null;
	private Boolean isSync = true;
	
	private ScriptPluginFactory pluginFactory = null;


	//是否重命名
	private boolean rename = true;
	
	private static Map<String, String> ImgPosMap = null;
	
	/** 用于图片压缩的相关参数 **/
	private Boolean needCompress = false;	// 是否需要压缩
	private String maxFilesize = null;			// 最大文件大小
	private String maxQuality = null;			// 最大压缩质量
	
	static {
		ImgPosMap = new HashMap<String, String>();
		ImgPosMap.put("leftTop", "northeast");
		ImgPosMap.put("rightTop", "northeast");
		ImgPosMap.put("leftBtm", "southwest");
		ImgPosMap.put("rightBtm", "southeast");
		ImgPosMap.put("center", "center");
		ImgPosMap.put("tile", "tile");
	}
	
	/*public String send(){
		
		if(url == null || url.equals("")){
			hasError = true;
			msg = "url is null";
			return "msg";
		}
		
		if(filedata == null || filedata.length() == 0 || filedataFileName == null){
			msg = "未找到上传的文件";
			hasError = true;
			return "msg";
		}
		
		try{
			SendFilePlugin sendFile = (SendFilePlugin)pluginFactory.getP("sendFile");
			sendFile.setDir("");
			sendFile.sendFile(filedata.getAbsolutePath(), url, syncflag);
			msg = url;
			log.info("file upload & send-send() : " + filedataFileName + " | url : " + msg);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}*/
	
	/**
	 * 上传并分发
	 * @return
	 */
	/*public String file(){
		
		if(domain == null || domain.equals("")){
			hasError = true;
			msg = "domain is null";
			return "msg";
		}
		
		if(filedata == null || filedata.length() == 0 || filedataFileName == null){
			msg = "未找到上传的文件";
			hasError = true;
			return "msg";
		}
		
		try{
			// 压缩文件
			compress();
			
			String file_ext = filedataFileName.substring(filedataFileName.lastIndexOf(".")).toLowerCase();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww");
			String fileurl = "http://" + domain + "/" +CmppConfig.getKey("cmpp.upload.sendfile.prefix")+
					"/"+sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+filedataFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase() +file_ext;
			
			SendFilePlugin sendFile = (SendFilePlugin)pluginFactory.getP("sendFile");
			sendFile.setDir("");
			sendFile.sendFile(filedata.getAbsolutePath(), fileurl, syncflag);
			msg = fileurl;
			bytes = filedata.length();
			log.info("file upload & send-file() : " + filedataFileName + " | url : " + msg);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "uploadResult";
	}*/
	
	/**
	 * 图片上传并处理、分发
	 * @return
	 */
	public String img(){
		
		//log.info(extradata);
		
		if(filedata == null || filedata.length() == 0 || filedataFileName == null){
			msg = "未找到上传的文件";
			hasError = true;
			return "msg";
		}
		
		String file_ext = filedataFileName.substring(filedataFileName.lastIndexOf(".")).toLowerCase();
		
		String filepath = CmppConfig.getKey("cmpp.upload.dir");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String filepath2 = sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+filedataFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase();
		File diskFile = new File(filepath, filepath2+file_ext);
		try{
			// 压缩文件
			compress();
			bytes = filedata.length();
			//处理上传空文件异常的情况
			if(filedata.length()==0){
				msg = "文件大小为0";
				hasError = true;
				return "msg";
			}else{
				if(!diskFile.exists()){
					if(!diskFile.getParentFile().exists()){
						diskFile.getParentFile().mkdirs();
					}
					filedata.renameTo(diskFile);
				}else{
					log.error("diskFile already exists");
				}
			}
			HttpServletRequest request=ServletActionContext.getRequest();
			String imageUrl = "http://"+request.getServerName()+":"+request.getServerPort() + CmppConfig.getKey("cmpp.upload.fileurl.prefix")+filepath2+file_ext;
			log.info("file upload-img() : " + filedataFileName + " | url : " + imageUrl);
			
			ImagePlugin imgPlugin = new ImagePlugin();
			imgPlugin.setImageURL(CmppConfig.getKey("image.URL"));
			imgPlugin.setImageServerName(CmppConfig.getKey("image.ServerName"));
			JSONObject obj = new JSONObject(extradata);
			Boolean isSync = obj.getBoolean("isSync");
			
			//Boolean isDistributed = obj.getBoolean("isDistributed");
			String distributeURL = obj.getString("distributeURL");
			String watermarkUrl = obj.getString("watermarkUrl");
			String watermarkPos = obj.getString("watermarkPos");
			
			String bigDimen = obj.getString("bigDimen");
			
			JSONObject resultJSON=new JSONObject();
			Map<String,String> result = null;
			Integer width;
			Integer height;
			if(bigDimen != null && !bigDimen.equals("")){
				JSONObject bigDimenJsonObj = new JSONObject(bigDimen);
				width= bigDimenJsonObj.getInt("width");
				height = bigDimenJsonObj.getInt("height");
				result=imgPlugin.cut(imageUrl,  width.toString(),height.toString(), null, distributeURL, isSync.toString(), null, watermarkUrl, watermarkPos);
				log.info("upload&img ret_url : " + result.get("result"));
				msg = result.get("result").toString();
				resultJSON.put("bigDimen", result.get("result"));
			}
			String thumbListString = obj.getString("thumbList");
			JSONArray thumbList=new JSONArray(thumbListString);
			JSONArray thumb=new JSONArray();
			for(int i=0;i<thumbList.length();i++){
				height=thumbList.getJSONObject(i).getInt("height");
				width= thumbList.getJSONObject(i).getInt("width");
				result=imgPlugin.cut(imageUrl, width.toString(),height.toString(), null, distributeURL, isSync.toString(), null, watermarkUrl, watermarkPos);
				thumb.put(result.get("result").toString());
			}
			resultJSON.put("thumbList", thumb);
			msg=resultJSON.toString();
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		
		return "uploadResult";
	}
	
	/*
	 * 加水印
	 */
	public String waterMark(){
		try {
			if(StringUtil.isBlank(url)){
				msg = "源图片地址不能为空";
				hasError = true;
				return "msg";
			}
			if(StringUtil.isBlank(domain)){
				domain = url.replace("http://", "").split("/")[0];
			}
			
			if(StringUtil.isBlank(position)){
				position="southeast";
			}
			if(StringUtil.isBlank(waterMarkUrl)){
				msg = "水印图片地址不能为空";
				hasError = true;
				return "msg";
			}

			ImagePlugin imagePlugin = (ImagePlugin)pluginFactory.getP("image");
			Map<String,String> resultMap = imagePlugin.waterMark(url, waterMarkUrl, position,domain,null,null);
			if(!Boolean.valueOf(resultMap.get("isHandleSuccess"))){
				msg = "处理不成功";
				hasError = true;
				return "msg";
			}
			if(!Boolean.valueOf(resultMap.get("isSendSuccess"))){
				msg = "分发不成功";
				hasError = true;
				return "msg";
			}
			msg = resultMap.get("result");

		} catch (IOException e) {
			msg = "出现异常 " + e.getMessage();
			hasError = true;
			return "msg";
		} catch (DocumentException e) {
			msg = "出现异常 " + e.getMessage();
			hasError = true;
			return "msg";
		}
		return "msg";
	}
	/**
	 * 图片上传并处理、分发
	 * @return
	 */
	public String img2(){
		
		if(filedata == null || filedata.length() == 0 || filedataFileName == null){
			msg = "未找到上传的文件";
			hasError = true;
			return "msg";
		}
		
		try{
			// 压缩文件
			compress();
						
			String file_ext = filedataFileName.substring(filedataFileName.lastIndexOf(".")).toLowerCase();
			
			String filepath = CmppConfig.getKey("cmpp.upload.dir");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String filepath2 = sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+filedataFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase();
			File diskFile = new File(filepath, filepath2+file_ext);
		
			bytes = filedata.length();
			//处理上传空文件异常的情况
			if(filedata.length()==0){
				msg = "文件大小为0";
				hasError = true;
				return "msg";
			}else{
				if(!diskFile.exists()){
					if(!diskFile.getParentFile().exists()){
						diskFile.getParentFile().mkdirs();
					}
					filedata.renameTo(diskFile);
				}else{
					log.error("diskFile already exists");
				}
			}
			HttpServletRequest request=ServletActionContext.getRequest();
			String imageUrl = "http://"+request.getServerName()+":"+request.getServerPort() + CmppConfig.getKey("cmpp.upload.fileurl.prefix")+filepath2+file_ext;
			log.info("file upload-img2() : " + filedataFileName + " | url : " + imageUrl);
			
			ImagePlugin imgPlugin = new ImagePlugin();
			imgPlugin.setImageURL(CmppConfig.getKey("image.URL"));
			imgPlugin.setImageServerName(CmppConfig.getKey("image.ServerName"));
			
			Map<String,String> result = imgPlugin.cut(imageUrl, width.toString(), height.toString(), null, domain, "false", null, null, null);
			log.info("upload&img2 ret_url : " + result);
			String isHandleSuccess = result.get("isHandleSuccess");
			String isSendSuccess = result.get("isSendSuccess");
			
			if(!isHandleSuccess.equals("true")){
				hasError = true;
				msg = "图片处理失败";
			}else{
				if(!isSendSuccess.equals("true")){
					hasError = true;
					msg = "图片分发失败";
				}else{
					msg = result.get("result").toString();
				}
			}
			
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		
		return "uploadResult";
	}
	
	public long getBytes() {
		return bytes;
	}

	public static void main(String[] args) throws Exception{
		ImagePlugin imgPlugin = new ImagePlugin();
		imgPlugin.setImageURL("220.181.67.235:8080");
		imgPlugin.setImageServerName("Cmpp_Image");
		Map<String,String> result = imgPlugin.cut("http://y1.ifengimg.com/uradio/resources/images/specialPage/startFromYou.jpg", "20", "20", null, "y0.ifengimg.com", "false", null, null, null);
		log.info("upload&img2 ret_url : " + result);
	}
	
	
	/**
	 * 仅上传
	 */
	public String execute(){
		
		if(filedata == null || filedata.length() == 0 || filedataFileName == null){
			msg = "未找到上传的文件";
			hasError = true;
			return "msg";
		}
		
		File diskFile = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww");
		String filepath = CmppConfig.getKey("cmpp.upload.dir");
		String filepath2 = "";
		String file_ext = "";
		if(rename){
			file_ext = filedataFileName.substring(filedataFileName.lastIndexOf(".")).toLowerCase();
			filepath2 = sdf.format(new Date()) + "/" + MD5.encode(System.currentTimeMillis()+filedataFileName+new Random().nextInt(1000)).substring(0, 15).toLowerCase();
			diskFile = new File(filepath, filepath2+file_ext);
		}else{
			filepath2 = sdf.format(new Date()) + "/" + filedataFileName;
			diskFile = new File(filepath, filepath2);
		}
		try{
			// 压缩文件
			compress();
			bytes = filedata.length();
			//处理上传空文件异常的情况
			if(filedata.length()==0){
				diskFile.getParentFile().mkdirs();
				diskFile.createNewFile();
			}else{
				if(!diskFile.exists()){
					if(!diskFile.getParentFile().exists()){
						diskFile.getParentFile().mkdirs();
					}
				}
				filedata.renameTo(diskFile);
			}
			msg = CmppConfig.getKey("cmpp.upload.fileurl.prefix")+filepath2+file_ext;
			log.info("file upload : " + filedataFileName + " | url : " + msg);
		}catch(Exception e){
			log.error(e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "uploadResult";
	}
	
	/**
	 * 满足条件，进行压缩
	 * @throws Exception
	 */
	private void compress() throws Exception{
		if (needCompress && ImageCompressUtil.isNeedCompress(filedata, new String[] { "jpeg" }, maxFilesize, maxQuality)) {
			File destFile = new File(filedata.getAbsolutePath() + ".jpg");
			ImageCompressUtil.compressQuality(filedata.getAbsolutePath(), destFile.getAbsolutePath());
			if (filedata.length() > destFile.length()) {
				filedata.delete();
				destFile.renameTo(filedata);
			} else {
				destFile.delete();
			}
		}
	}

	public void setFiledata(File filedata) {
		this.filedata = filedata;
	}

	public void setFiledataFileName(String filedataFileName) {
		this.filedataFileName = filedataFileName;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setExtradata(String extradata) {
		this.extradata = extradata;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setRename(boolean rename) {
		this.rename = rename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSyncflag() {
		return syncflag;
	}

	public void setSyncflag(String syncflag) {
		this.syncflag = syncflag;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Boolean getIsSync() {
		return isSync;
	}

	public void setIsSync(Boolean isSync) {
		this.isSync = isSync;
	}
	
	public String getWaterMarkUrl() {
		return waterMarkUrl;
	}

	public void setWaterMarkUrl(String waterMarkUrl) {
		this.waterMarkUrl = waterMarkUrl;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	public ScriptPluginFactory getPluginFactory() {
		return pluginFactory;
	}

	public void setPluginFactory(ScriptPluginFactory pluginFactory) {
		this.pluginFactory = pluginFactory;
	}

	public Boolean getNeedCompress() {
		return needCompress;
	}

	public void setNeedCompress(Boolean needCompress) {
		this.needCompress = needCompress;
	}

	public String getMaxFilesize() {
		return maxFilesize;
	}

	public void setMaxFilesize(String maxFilesize) {
		this.maxFilesize = maxFilesize;
	}

	public String getMaxQuality() {
		return maxQuality;
	}

	public void setMaxQuality(String maxQuality) {
		this.maxQuality = maxQuality;
	}
}
