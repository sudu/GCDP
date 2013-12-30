package com.me.GCDP.action.upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.me.GCDP.action.upload.util.FileOperator;
import com.me.GCDP.action.upload.util.UploadUtil;
import com.me.GCDP.model.UploadFile;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

public class PauseResumeUploadAction extends ActionSupport
{
	private static final long	serialVersionUID	= 7843051003047272901L;
	
	private static Log log = LogFactory.getLog(PauseResumeUploadAction.class);
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	/*************** query ***************/
	
	private String				fileid;
	private String				fileId;
	private String				type;

	/************* upload file ***************/
	
	private File				filedata;
	private String				detail;
	private boolean				completed;
	private int					isUpload;
	private int					error;
	private long				length;
	private String				filename;
	private boolean				lb;
	private String				checksum;
	private boolean				format;
	
	private String 				url;
	private long 				start;
	
	private long fileSize;
	
	@Autowired
	@Qualifier("fileOperator")
	private FileOperator		fileOperator;

	@Autowired
	@Qualifier("uploadUtil")
	private UploadUtil			uploadUtil;

	public File getFiledata()
	{
		return filedata;
	}

	public void setFiledata(File filedata)
	{
		this.filedata = filedata;
	}

	public String getChecksum()
	{
		return checksum;
	} 

	public void setChecksum(String checksum)
	{
		this.checksum = checksum;
	}

	public String getFileid()
	{
		return fileid;
	}

	public void setFileid(String fileid)
	{
		this.fileid = fileid;
	}

	public boolean isCompleted()
	{
		return completed;
	}

	public void setCompleted(boolean completed)
	{
		this.completed = completed;
	}

	public int getIsUpload()
	{
		return isUpload;
	}

	public void setIsUpload(int isUpload)
	{
		this.isUpload = isUpload;
	}

	public int getError()
	{
		return error;
	}

	public void setError(int error)
	{
		this.error = error;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(long length)
	{
		this.length = length;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public boolean isLb()
	{
		return lb;
	}

	public void setLb(boolean lb)
	{
		this.lb = lb;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isFormat()
	{
		return format;
	}

	public void setFormat(boolean format)
	{
		this.format = format;
	}

	public String getFileId()
	{
		return fileId;
	}

	public void setFileId(String fileId)
	{
		this.fileId = fileId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}
	
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String queryUpload()
	{
		// 文件从未上传过
		if (!fileOperator.checkFile(fileid))
		{
			// 通知前端
			length 		= 0;
			checksum 	= null;
			completed 	= false;
			isUpload 	= 0;
			error 		= 0;
			detail 		= null;
			fileId 		= fileid; // fileid 传输到请求上传的客户端
			
			// 写入REDIS CACHE
			UploadFile uploadFile = new UploadFile();
			uploadFile.setFileid(fileid);
			uploadFile.setFilename(filename);
			uploadFile.setType(type);
			uploadFile.setFilepath(fileOperator.createUploadPath(fileid + type)); // type: '.xxx'
			uploadFile.setLength(length + "");
			uploadFile.setChecksum(checksum == null ? "" : checksum	);
			uploadFile.setCompleted(completed);
			uploadFile.setUpload(false);
			uploadFile.setError(false);
			uploadFile.setDetail("upload initialized");
			uploadFile.setUploadtime(new Date());
			
			uploadFile.setFileSize(fileSize+"");
			
			try
			{
				String fileInfoJsonStr = uploadUtil.assembleFileInfoJsonStr(uploadFile);
				fileOperator.putFileInfoInCache(fileid, fileInfoJsonStr);
			}
			catch (Exception ex)
			{
				log.error(fileid + " | initialize upload error: " + ex);
				error 	= 0;
				detail 	= "initialize upload error: " + ex;
				
				// 写入REDIS （待完成）
				
			}
			
		}
		// 文件已经上传过了
		else
		{
			// 默认值是0
			long savedFileLength = fileOperator.getSavedFileLength(fileid);
			
			log.info(fileid + " | 该文件已经上传了" + savedFileLength + "字节");
			
			try
			{
				// 查找REDIS，检查文件上传状态
				UploadFile uploadFile = new UploadFile();
				JSONObject fileInfoJsonObj = fileOperator.getFileInfoFromCache(fileid);
//				log.info("from redis: " + fileInfoJsonObj.toString());
				uploadFile = uploadUtil.assembleUploadFileModel(fileInfoJsonObj);
				if (uploadFile != null)
				{
					//当已存文件长度和实际文件长度相同则认为文件已经上传成功，无论缓存中状态为何
//					log.info("file size: "+fileSize);
					if(savedFileLength==fileSize){
						uploadFile.setFileSize(fileSize+"");
						uploadFile.setCompleted(true);
						String fileInfoJsonStr = uploadUtil.assembleFileInfoJsonStr(uploadFile);
//						log.info("into redis: " + fileInfoJsonStr);
						fileOperator.putFileInfoInCache(fileid, fileInfoJsonStr);
					}
					
					// 文件上传完了
					if (uploadFile.isCompleted() )
					{
						length 		= savedFileLength;
						completed 	= true;
						isUpload	= 1;
						error 		= 0;
						detail 		= null;
						fileId 		= fileid;
						format 		= true;		// 需要通过加密规则验证
						
						// 返回文件的URL
						String filepath = uploadUtil.getFieldInfo("filepath", fileInfoJsonObj);
						filepath 		= filepath.replace(fileOperator.getScriptPrivateDir(), "");
						url 			= filepath; // 文件存放路径
						
						log.info(fileid + " | query - 文件URL： " + url);
						
					}
					// 中断传输的文件， 即没有传输完的文件 -- 没有录入DB
					if (!uploadFile.isCompleted())
					{
						length 		= savedFileLength;
						checksum 	= null;
						completed 	= false;
						isUpload 	= 1;
						error 		= 0;
						detail 		= null;
						fileId 		= fileid;
						format 		= true;		// 需要通过加密规则验证
					}
				}
				// 目前不可能是空值
				else
				{
					length 		= savedFileLength;
					checksum 	= null;
					completed	= false;
					isUpload 	= 1;
					error 		= 0;
					detail 		= null;
					fileId 		= fileid;
					format 		= true;		// 需要通过加密规则验证
				}
			}
			catch (Exception ex)
			{
				error 	= 0;
				detail 	= "initialize upload error2: " + ex;
			}
		}
		
		// 设置RESPONSE响应头， 解决AJAX跨域访问
		uploadUtil.setHeaders();
		
		return SUCCESS;
	}
	
	public String uploadFile() throws Exception
	{
		/**************** 过滤OPTIONS方法， 实现AJAX跨域访问 ******************/
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response =  ServletActionContext.getResponse();
		String method = request.getMethod();
		if ("OPTIONS".equalsIgnoreCase(method))
		{
			response.setHeader("OPTIONS", "OPTIONS");
			uploadUtil.setHeaders();
			return SUCCESS;
		}
		
		/************************** 核心代码： 上传文件 **********************/
		try
		{
			filename = uploadUtil.filterCoding(filename); // 过滤 filename 编码
			
			/*********** 追加文件 ***********/
			
			lock.lock(); // 写同步
			
			/*
			 * 从redis中取出文件长度（length字段）， 如果文件长度与start值相同， 就开始写文件，
			 * 不同，则设置error = true, format=false， 返回
			 */
			JSONObject fileInfoJsonObj = fileOperator.getFileInfoFromCache(fileid);
			String filepath = uploadUtil.getFieldInfo("filepath", fileInfoJsonObj);
			String savedLength = uploadUtil.getFieldInfo("length", fileInfoJsonObj);
			
			if (start == Long.parseLong(savedLength))
			{
				String uploadFileRet = fileOperator.uploadFile(fileid, type, filedata);
				
				log.info(fileid + " | upload params: {fileid:" + fileid + ",filename:"
						+ filename + ",filepath:" + filepath + ",type:" + type
						+ ",checksum:" + checksum + ",length:" + length
						+ ",completed:" + completed + ",isUpload:" + isUpload
						+ ",error:" + error + ",format:" + format + ",detail:"
						+ detail + ",uploadtime:"
						+ new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date())
						+ ",lb:" + lb + ",start:" + start + "}");
				
				// 如果是当前上传的数据不是文件的最后一块，则通知客户端继续上传。
				if (!lb)
				{
					// 文件写入正确
					if (1 == Integer.parseInt(uploadUtil.getPartialRet("ret", uploadFileRet)))
					{
						fileId 		= fileid;
						completed 	= false;
						length 		= Long.parseLong(uploadUtil.getPartialRet("length", uploadFileRet));
						isUpload 	= 1;
						format 		= true;
						error 		= 0;
						detail 		= "";
						
						uploadUtil.setHeaders();
						
						// 写入REDIS缓存
						JSONObject jsonObj =  new JSONObject();
						jsonObj.put("fileid",  	fileid);
						jsonObj.put("filename", filename == null ? "\'\'" : filename);
						
						String savedFilePath = fileOperator.createUploadPath(fileid + type);
						jsonObj.put("filepath", savedFilePath == null ? "\'\'" : savedFilePath);
						
						jsonObj.put("type", 	type == null ? "\'\'" : type);
						jsonObj.put("checksum", checksum == null ? "\'\'" : checksum);
						jsonObj.put("length", 	length);
						jsonObj.put("completed",completed);
						jsonObj.put("isUpload", isUpload==1);
						jsonObj.put("error", 	error);
						jsonObj.put("detail", 	detail == null ? "" : detail);
						SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
						fileInfoJsonObj.put("uploadtime", formatter.format(new Date()));
						
						jsonObj.put("fileSize", fileSize);
						
						fileOperator.putFileInfoInCache(fileid, jsonObj.toString());
						
					}
					// 文件写入错误， 如磁盘已满等
					else
					{
						// do nothing temporarily
					}
					
				}
				// 如果是当前上传的数据是文件的最后一块，则 
				// 1. 校验是否为同一文件；-- （此处略去）
				// 2.写入数据库文件信息；
				// 3.通知客户端文件已经上传完毕。
				else
				{
					// 文件信息写入数据库
					if (1 == Integer.parseInt(uploadUtil.getPartialRet("ret", uploadFileRet)))
					{
						/** 校验（暂时不写此功能） **/
						// checksum = checksum 是文件前512k（不足则取全部）和 文件长度进行sha1加密后的字符串
						// fileid 是" 用户名 + 应用名  + checksum" 的校验码： fileid = SHA1.hash(uid+"boke"+checksum);
						
						/** 将上传的文件状态信息写入REDIS缓存 **/
						
						UploadFile uploadFile = new UploadFile();
						uploadFile.setFileid(fileid);
						uploadFile.setFilename(filename);
						uploadFile.setType(type);
						uploadFile.setFilepath(filepath);
						uploadFile.setChecksum(checksum);
						uploadFile.setLength(uploadUtil.getPartialRet("length", uploadFileRet));
						uploadFile.setCompleted(true);
						uploadFile.setUpload(true);
						uploadFile.setError(false);
						uploadFile.setDetail(""); // 以后会添加
						uploadFile.setUploadtime(new Date());
						String fileInfoJsonStr = uploadUtil.assembleFileInfoJsonStr(uploadFile);
						fileOperator.putFileInfoInCache(fileid, fileInfoJsonStr);
						
						/** 修改状态，告诉客户端上传信息 **/
						
						// 返回给前端的URL
						// 返回的目录是绝对目录， 外界拿到目录可以访问到已经上传的文件
						filepath 	= filepath.replace(fileOperator.getScriptPrivateDir(), "");
						
						length 		= Long.parseLong(uploadUtil.getPartialRet("length", uploadFileRet)); 
						completed 	= true;
						isUpload  	= 1;
						error 		= 0;
						format 		= true; 	// 注意：需要验证， 明天弄清楚如何验证
						fileId 		= fileid;	// fileid
						url 		= filepath; // 文件存放路径
						
						// log.info("query - 文件URL： " + url);
						
						log.info(fileid+" | 文件上传完成时的参数 --> {length:" + length + ", url:" + url + "}");
						
						uploadUtil.setHeaders();
					}
					else
					{
						// do nothing
					}
				}
				
			}
			else
			{
				
				/*
				 * 返回正常的数据 
				 */
				/*fileId 		= fileid;
				completed 	= false;
				length 		= Integer.parseInt(savedLength);
				isUpload 	= 1;
				format 		= true;
				error 		= 0;
				detail 		= "";
				
				filepath 	= filepath.replace(fileOperator.getScriptPrivateDir(), "");
				url 		= filepath;
				
				uploadUtil.setHeaders();*/
				
				/*
				 * 返回错误信息
				 */
				error 		= 1;
				detail 		= "文件上传错误 文件大小不匹配 ";
				length 		= 0;
				
				log.info(fileid + " | 文件上传错误 文件大小不匹配 --> " + filename);
				
//				RedisCache redisCache = (RedisCache) SpringContextUtil.getBean("redisCache");
//				redisCache.remove(fileid);
//				File errorfile = new File(filepath);
//				if(errorfile.exists()){
//					errorfile.delete();
//					log.info("错误文件删除: " + filename);
//				}
				
				
				
			}
			
		}
		catch (Exception ex)
		{
			log.error(fileid + " | 服务器端错误：" + ex.getMessage());
			error 	= 1;
			detail 	= "服务器端错误：" + ex.getMessage();
		}
		finally
		{
			lock.unlock();
		}
		
		return SUCCESS;
	}
	
}
