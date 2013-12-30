package com.me.GCDP.action.upload.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.model.UploadFile;
import com.me.json.JSONException;
import com.me.json.JSONObject;

public class UploadUtil
{
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static Log	log	= LogFactory.getLog(UploadUtil.class);
	
	public UploadUtil() {
		super();
	}

	/**
	 * convertResult 主要用于上传动作
	 * @param ret   （上传动作的）执行结果： 0-->失败；1-->成功
	 * @param msg   success-->成功； 失败
	 * @return
	 */
	public static String convertResult(int ret, String msg, long length)
	{
		JSONObject result = new JSONObject();

		try
		{
			result.put("ret", ret);
			result.put("msg", msg);
			result.put("length", length); // 记录已经上传的文件长度
		}
		catch (JSONException ex)
		{
			log.error(ex.getMessage());
		}

		return result.toString();
	}

	public String getPartialRet(String name, String result)
	{
		try
		{
			JSONObject retStr = new JSONObject(result);
			return retStr.getString(name);
		}
		catch (JSONException ex)
		{
			log.error(ex.getMessage());
		}
		return null;
	}

	public void setHeaders()
	{
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "30");
	}
	
	public String filterCoding(String source)
	{
		String ret = "";
		
		if (source == null || "".equals(source)) return ret;
		
		try
		{
			ret = new String(source.getBytes("ISO8859-1"), "UTF-8");
		}
		catch (UnsupportedEncodingException ex)
		{
			log.error("error occurs when changing coding: " + ex);
		}
		
		return ret;
	}
	
	public String assembleFileInfoJsonStr(UploadFile uploadFile)
	{
		JSONObject fileInfoJsonObj =  new JSONObject();
		
		try
		{
			fileInfoJsonObj.put("fileid",  	uploadFile.getFileid());
			fileInfoJsonObj.put("filename", uploadFile.getFilename() 	== null ? "" : uploadFile.getFilename());
			fileInfoJsonObj.put("filepath", uploadFile.getFilepath()	== null ? "" : uploadFile.getFilepath());
			fileInfoJsonObj.put("type", 	uploadFile.getType() 		== null ? "" : uploadFile.getType());
			fileInfoJsonObj.put("checksum", uploadFile.getChecksum() 	== null ? "" : uploadFile.getChecksum());
			fileInfoJsonObj.put("length", 	uploadFile.getLength()		== null ? "" : uploadFile.getLength()+"");
			fileInfoJsonObj.put("completed",uploadFile.isCompleted());
			fileInfoJsonObj.put("isUpload", uploadFile.isUpload());
			fileInfoJsonObj.put("error", 	uploadFile.isError());
			fileInfoJsonObj.put("detail", 	uploadFile.getDetail());
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
			fileInfoJsonObj.put("uploadtime", formatter.format(uploadFile.getUploadtime()));
			
			fileInfoJsonObj.put("fileSize",uploadFile.getFileSize() == null ? "" :uploadFile.getFileSize());
		}
		catch (JSONException e) 
		{
			log.error(uploadFile.getFileid() + " | " + e.getMessage());
		}
		
		return fileInfoJsonObj.toString();
	}
	
	public UploadFile assembleUploadFileModel(JSONObject fileInfoJsonObj)
	{
		UploadFile uploadFile = new UploadFile();
		String fileid = null; 
		
		try 
		{
			fileid = fileInfoJsonObj.getString("fileid");
			uploadFile.setFileid(fileInfoJsonObj.getString("fileid"));
			uploadFile.setFilename(fileInfoJsonObj.getString("filename"));
			uploadFile.setFilepath(fileInfoJsonObj.getString("filepath"));
			uploadFile.setType(fileInfoJsonObj.getString("type"));
			uploadFile.setChecksum(fileInfoJsonObj.getString("checksum"));
			uploadFile.setLength(fileInfoJsonObj.getString("length"));
			
			try{
				uploadFile.setCompleted(fileInfoJsonObj.getBoolean("completed"));
			}catch(JSONException e){
				boolean completed = fileInfoJsonObj.getInt("completed")==1;
				uploadFile.setCompleted(completed);
				log.error("completed convert");
			}
			
			try{
				uploadFile.setUpload(fileInfoJsonObj.getBoolean("isUpload"));
			}catch(JSONException e){
				boolean isUpload = fileInfoJsonObj.getInt("isUpload")==1;
				uploadFile.setUpload(isUpload);
				log.error("isUpload convert");
			}
			
			try{
				uploadFile.setError(fileInfoJsonObj.getBoolean("error"));
			}catch(JSONException e){
				boolean error = fileInfoJsonObj.getInt("error")==1;
				uploadFile.setError(error);
				log.error("error convert");
			}
			
			uploadFile.setDetail(fileInfoJsonObj.getString("detail"));
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
			Date date;
			if(fileInfoJsonObj.has("uploadtime")){
				try 
				{
					date = formatter.parse(fileInfoJsonObj.getString("uploadtime"));
					uploadFile.setUploadtime(date);
				} 
				catch (ParseException e) 
				{
					String filePath = fileInfoJsonObj.getString("filepath") + File.separator + fileInfoJsonObj.getString("fileid");
					log.error(fileid + "error occurs when parsing upload date for " + filePath + ": " + e);
				}
			}else{
				uploadFile.setUploadtime(new Date());
			}
			
			uploadFile.setFileSize(fileInfoJsonObj.has("fileSize")?fileInfoJsonObj.getString("fileSize"):"0");

			
			
		} 
		catch (JSONException e) 
		{
			log.error(fileid + " | " + e.toString()+" | fileInfoJsonObj: " + fileInfoJsonObj);
			e.printStackTrace();
			
		}
		
		return uploadFile;
	}
	
	public String getFileExtension(String filename)
	{
		String extension = null;
		
		if (filename != null)
		{
			int position = filename.lastIndexOf(".");
			extension = filename.substring(position);
		}
		else
		{
			extension = "";
		}
		
		return extension;
	}
	
	public String getFieldInfo(String fieldName, JSONObject fileInfoJsonObj)
	{
		String fieldValue = null;
		try
		{
			fieldValue = fileInfoJsonObj.getString(fieldName);
		}
		catch (JSONException ex)
		{
			log.error("get field value from JSONObject:" + ex);
		}
		
		return fieldValue;
	}

}
