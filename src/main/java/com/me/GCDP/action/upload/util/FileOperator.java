package com.me.GCDP.action.upload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.RedisCache;
import com.me.json.JSONException;
import com.me.json.JSONObject;


public final class FileOperator
{
	private static Log log = LogFactory.getLog(FileOperator.class);
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/** 由断点续传的上传文件存放的目录 **/
	private String scriptPrivateDir;
	
	/** 由断点续传的文件存放文件夹名 **/
	private String savedFolder;
	
	private RedisCache redisCache;
	
	private String timeout;
	
	private UploadUtil uploadUtil;
	
	public String getScriptPrivateDir() {
		return scriptPrivateDir;
	}

	public void setScriptPrivateDir(String scriptPrivateDir) {
		this.scriptPrivateDir = scriptPrivateDir;
	}

	public String getSavedFolder() {
		return savedFolder;
	}

	public void setSavedFolder(String savedFolder) {
		this.savedFolder = savedFolder;
	}
	
	public void setRedisCache(RedisCache redisCache) {
		this.redisCache = redisCache;
	}

	public RedisCache getRedisCache() {
		return redisCache;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public UploadUtil getUploadUtil() {
		return uploadUtil;
	}

	public void setUploadUtil(UploadUtil uploadUtil) {
		this.uploadUtil = uploadUtil;
	}

	public String getYear()
	{
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR) + "";
	}
	
	public String getMD()
	{
		Calendar calendar = Calendar.getInstance();
		
		String monthStr = null;
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) monthStr = "0" + month;
		else monthStr = month + "";
		
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayStr = null;
		if (day < 10) dayStr = "0" + day;
		else dayStr = day + "";
		
		return  monthStr + dayStr;
	}
	
	// checksum = checksum 是文件前512k（不足则取全部）和 文件长度进行sha1加密后的字符串
	// fileid 是" 用户名 + 应用名  + checksum" 的校验码： fileid = SHA1.hash(uid+"boke"+checksum);
	public String uploadFile(String fileid, String filetype, File file) 
	{
		FileInputStream fis = null; 
		RandomAccessFile savedFile = null;
		long length = 0; 
		
		String filePath = null;
		
		try
		{
			/** 写入磁盘 **/
			fis = new FileInputStream(file); 
			
			// 从缓存中查找， 若为空则创建
			JSONObject fileInfoJsonObj = getFileInfoFromCache(fileid);
			
			if (fileInfoJsonObj == null) 
				filePath = createUploadPath(fileid + filetype);
			else 
				filePath = fileInfoJsonObj.getString("filepath");
			
			if (filePath == null) // 可能性极小
			{

				// 20130318
				String savedPath = scriptPrivateDir + File.separator + savedFolder;
				File fileDir = new File(savedPath);
				if (!fileDir.exists()) fileDir.mkdirs();
				
				// 创建“年”文件夹
				String yearDirPath = savedPath + File.separator + getYear();
				File yearDir = new File(yearDirPath);
				if (!yearDir.exists()) yearDir.mkdirs();
				
				// 创建“月”文件夹
				String yearMonthDirPath = yearDirPath + File.separator + getMD();
				File monthDir = new File(yearMonthDirPath);
				if (!monthDir.exists()) monthDir.mkdirs();
				
				filePath = yearMonthDirPath + File.separator + fileid;
			}
			
			savedFile = new RandomAccessFile(filePath, "rw"); 
			
			length = savedFile.length(); 			// 获取已经保存的文件大小
			savedFile.seek(length); 				// 定位文件指针到length位置 
			
			byte[] bytes = new byte[1024]; 
			int numRead; 
			while ((numRead = fis.read(bytes, 0, 1024)) > 0) 
			{
				savedFile.write(bytes, 0, numRead);
			}
			
			length = savedFile.length(); 			// 记录已经上传（可能是最后）的文件长度
			
			log.debug(fileid+ " | 磁盘上文件的实际长度： " + length);
			
		}
		catch (Exception ex)
		{
			log.error(fileid+ " | error occurs when writing file into disk: " + ex.getMessage());
			return UploadUtil.convertResult(0, ex.getMessage(), length);
		}
		finally
		{
			try
			{
				if (fis != null) fis.close();
				if (savedFile != null) savedFile.close();
			}
			catch (Exception ex)
			{
				log.error(fileid+ "RandomAccessFile file closing error: " + ex);
			}
		}
		
		return UploadUtil.convertResult(1, "success", length);
	}
	
	public long getSavedFileLength(String fileid)
	{
		// 默认值是0
		long length = 0;
		
		// 从缓存获取file path 信息
		// 有可能为空， 需在文件写入磁盘时，写入缓存
		JSONObject fileInfoJsonObj = getFileInfoFromCache(fileid);
		
		if (fileInfoJsonObj == null) return length;
		
		String fileSavedPath = uploadUtil.getFieldInfo("filepath", fileInfoJsonObj);
		
		log.info(fileid+ " | counting the size of file " + fileSavedPath + File.separator + fileid);
		
		// 文件存放根目录 20130318
		String savedPath = scriptPrivateDir + File.separator + savedFolder;
		File fileDir = new File(savedPath);
		if (!fileDir.exists()) fileDir.mkdirs();
		
		RandomAccessFile savedFile = null;
		try
		{
			savedFile = new RandomAccessFile(fileSavedPath, "r");
			length = savedFile.length();
		}
		catch (FileNotFoundException ex)
		{
			log.error(fileid+ " | no file " + fileid + " in " + savedPath + " : " + ex);
		}
		catch (IOException ex)
		{
			log.error(fileid+ " | error occurs when accessing file " + savedPath + File.separator + fileid + ": " + ex);
		}
		finally
		{
			if (savedFile != null)
			{
				try
				{
					savedFile.close();
				}
				catch (Exception ex)
				{
					log.error(fileid+ " | " + ex);
				}
			}
		}
		
		return length;
	}
	
	public boolean checkFile(String fileid)
	{
		boolean ret = true;
		
		/*
		 * 检查缓存，再检查磁盘
		 * A: 缓存有， 从缓存取出文件信息（路径），检查路径是否存在， 
		 * 		a. 路径存在（文件在），ret = true ( 取出文件大小 )；
		 * 		b. 路径不存在（已删除）， ret = false ( 则重新上传 )
		 * B: 缓存无， ret = false ( 重新上传 )
		 */
		// RandomAccessFile savedFile = null;
		try
		{
			JSONObject fileInfoJsonObj = getFileInfoFromCache(fileid);
			// 缓存有文件信息
			if (fileInfoJsonObj != null)
			{
				String filePath = fileInfoJsonObj.getString("filepath");
				// way1
				/*try
				{
					savedFile = new RandomAccessFile(filePath, "r");
				}
				catch (FileNotFoundException ex)
				{
					// 此处用info级别
					log.info("file " + fileid + " doesn't exist.");
					ret = false;
				}*/
				
				// way2
				File file = new File(filePath);
				if (!file.exists())
				{
					ret = false;
				}
			}
			// 缓存无文件信息
			else
			{
				ret = false;
			}
		}
		catch (Exception ex)
		{
			log.error(fileid+ " | parse file info in redis: " + ex);
		}
		/*finally
		{
			try
			{
				if (savedFile != null) savedFile.close();
			}
			catch (Exception ex)
			{
				log.error("" + ex);
			}
		}*/
		
		return ret;
	}
	
	/**
	 * truncateFileHeader 
	 * 获取文件的前512K
	 * @return 文件前512k（不足则取全部）
	 */
	public byte[] truncateFileHeader(String savePath, String fileName)
	{
		byte[] bytes = new byte[512 * 1024];
		RandomAccessFile randomAccessFile = null;
		try
		{
			String filePath = savePath + File.separator + fileName;
			randomAccessFile = new RandomAccessFile(filePath, "r");
			int beginIndex = 0;
			randomAccessFile.seek(beginIndex);
			int byteread = 0;
			// 一次读512 * 1024个字节，如果文件内容不足512 * 1024 个字节 （512K），则读剩下的字节。
			// 将一次读取的字节数赋给byteread
			// 524288 = 512*1024
			while ((byteread = randomAccessFile.read(bytes)) != -1)
			{
				// 此处代码不需要
				if (byteread == 512 * 1024) break;
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (randomAccessFile != null)
			{
				try
				{
					randomAccessFile.close();
				}
				catch (IOException ex)
				{
					log.error(fileName+ " | "+ex);
				}
			}
		}

		return bytes;
	}
	
	/**
	 * 从REDIS中取出文件信息JSON字串， 并转换证JSON对象
	 */
	public JSONObject getFileInfoFromCache(String fileid)
	{
		JSONObject jsonObj = null;
		String fileInfoJsonStr = redisCache.get(fileid);
		
		if (fileInfoJsonStr != null)
		{
			try 
			{
				jsonObj = new JSONObject(fileInfoJsonStr);
			} 
			catch (JSONException ex) 
			{
				log.error(fileid+ " | transfer data in redis to JSONObject: " + ex);
			}
		}
		
		return jsonObj;
	}
	
	/**
	 * 往REDIS中放入文件信息的JSON字串
	 */
	public void putFileInfoInCache(String fileid, String fileInfoJsonStr)
	{
		redisCache.put(fileid, fileInfoJsonStr, Integer.parseInt(timeout));
	}
	
	public String createUploadPath(String savedFileName)
	{
		String filePath = null;
		
		// 20130318
		String savedPath = scriptPrivateDir + File.separator + savedFolder;
		
		log.info("savedPath: " + savedPath);
		
		File fileDir = new File(savedPath);
		if (!fileDir.exists()) fileDir.mkdirs();
		
		String yearDirPath = savedPath + File.separator + getYear();
		File yearDir = new File(yearDirPath);
		if (!yearDir.exists()) yearDir.mkdirs();
		
		// 创建“月”文件夹
		String yearMonthDirPath = yearDirPath + File.separator + getMD();
		File monthDir = new File(yearMonthDirPath);
		if (!monthDir.exists()) monthDir.mkdirs();
		
		filePath = yearMonthDirPath + File.separator + savedFileName;
		
		log.info(savedFileName + " | filePath: " + filePath);
		
		return filePath;
	}
	
	public void updateFileInfo(String fileid, Map<String, Object> params)
	{
		try 
		{
			String fileInfoJsonStr = redisCache.get(fileid);
			if (fileInfoJsonStr != null)
			{
				JSONObject fileInfoJsonObj = new JSONObject(fileInfoJsonStr);
				Map<String, Object> fields = fileInfoJsonObj.toMap();
				fields.put("fileid", 	params.get("fileid"));
				fields.put("filename", 	params.get("filename"));
				fields.put("filepath", 	params.get("filepath"));
				fields.put("type", 		params.get("type"));
				fields.put("checksum", 	params.get("checksum"));
				fields.put("length", 	params.get("length"));
				fields.put("completed", params.get("completed"));
				fields.put("isUpload", 	params.get("isUpload"));
				fields.put("error", 	params.get("error"));
				fields.put("detail", 	params.get("detail"));
				SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
				fields.put("detail", 	formatter.format(new Date()));
				
				log.info("updateFileInfo: " + fields.toString());
				
				redisCache.put(fileid, fields, Integer.parseInt(timeout));
				
			}
		} 
		catch (JSONException e) 
		{
			log.error("transfer string to JSON data: " + e);
		}
	}
	
}


