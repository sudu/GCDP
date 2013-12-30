package com.me.GCDP.action.resource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.me.GCDP.action.resource.service.ResourceBizService;
import com.me.GCDP.mapper.ResourceFileMapper;
import com.me.GCDP.mapper.ResourceFolderMapper;
import com.me.GCDP.model.ResourceFile;
import com.me.GCDP.model.ResourceFolder;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.util.ImageCompressUtil;
import com.me.GCDP.util.MD5;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.opensymphony.xwork2.ActionSupport;
// import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 
 */
public class FileMgrAction extends ActionSupport
{
	private static final long	serialVersionUID	= 1245514566476361166L;
	
	private static Log log = LogFactory.getLog(FileMgrAction.class);
	
	// 页面跳转字串
	private static final String 	 MSG_PAGE 	= "msg";
	
	private static final List<String> domains 	= new ArrayList<String>();
	
	// private static final ReentrantLock   lock 	= new ReentrantLock();
	
	private static String uploadPrefix;
	
	/** 需要用户输入字串 **/
	private Integer	nodeId;
	
	private File    filedata; 
	private String  filedataContentType;  
	private String  fileName;
	
	private String  domain; 		// 旧的文件上传控件含有的字串
	private String 	syncflag;
	
	private String 	folderPath; 	// 输入的文件目录
	private Boolean distributionEnabled = true;						// 前端输入
	
	/** 用于返回的字段 **/
	private Boolean hasError   = false;
	private String 	msg;
	
	private Boolean fileExists = false;;
	private int 	reupload   = 0;
	
	
	@Autowired
	@Qualifier("fileMapper") 
	private ResourceFileMapper<ResourceFile> fileMapper;
	
	@Autowired
	@Qualifier("folderMapper")
	private ResourceFolderMapper<ResourceFolder> folderMapper;
	
	@Autowired
	@Qualifier("resourceBizService") 
	private ResourceBizService resourceBizService;
	
	@Autowired
	@Qualifier("pluginFactory") 
	private ScriptPluginFactory pluginFactory;
	
	static 
	{
		try
		{
			/** domain来源于配置文件 **/
			String domainsStr = CmppConfig.getKey("cmpp.resource.mgr.domains");
			String[] domainArray = domainsStr.split(",");
			for (String domain : domainArray)
			{
				domains.add(domain);
			}
			
			/** 初始化文件上传前缀 **/
			uploadPrefix = CmppConfig.getKey("cmpp.upload.sendfile.prefix");
		}
		catch(Exception ex)
		{
			log.error("initialize the configuration params: " + ex);
		}
	}
	
	public int getReupload()
	{
		return reupload;
	}

	public void setReupload(int reupload)
	{
		this.reupload = reupload;
	}

	public Boolean getFileExists()
	{
		return fileExists;
	}

	public void setFileExists(Boolean fileExists)
	{
		this.fileExists = fileExists;
	}

	public static String getUploadPrefix()
	{
		return uploadPrefix;
	}

	public static void setUploadPrefix(String uploadPrefix)
	{
		FileMgrAction.uploadPrefix = uploadPrefix;
	}

	public Boolean getDistributionEnabled()
	{
		return distributionEnabled;
	}

	public void setDistributionEnabled(Boolean distributionEnabled)
	{
		this.distributionEnabled = distributionEnabled;
	}

	public static List<String> getDomains()
	{
		return domains;
	}

	public int getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(Integer nodeId)
	{
		this.nodeId = nodeId;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public File getFiledata()
	{
		return filedata;
	}

	public void setFiledata(File filedata)
	{
		this.filedata = filedata;
	}

	public String getFiledataContentType()
	{
		return filedataContentType;
	}

	public void setFiledataContentType(String filedataContentType)
	{
		this.filedataContentType = filedataContentType;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public Boolean getHasError()
	{
		return hasError;
	}

	public void setHasError(Boolean hasError)
	{
		this.hasError = hasError;
	}

	public String getSyncflag()
	{
		return syncflag;
	}

	public void setSyncflag(String syncflag)
	{
		this.syncflag = syncflag;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	/*
	 * 功能说明： 默认目录上传，自定义目录上传， 自定义URL上传
	 * 
	 * 输入:     nodeId,domain,filedata,fileName,folderPath（用户输入的目录）
	 * 输出：           hasError, msg （说明： 如果没有出错， msg=文件分发地址|压缩后文件大小（若没有压缩就是原大小）；如果出错，就是出错信息）
	 */
	public String customDirUploadFile()
	{
		if (nodeId == null)
		{
			msg = "nodeid为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		if (domain == null || domain.equals(""))
		{
			hasError = true;
			msg = "domain is null";
			return MSG_PAGE;
		}
		
		/*if (filedata == null || filedata.length() == 0)
		{
			msg = "未找到上传的文件";
			hasError = true;
			return MSG_PAGE;
		}*/
		
		// 避免并发创建文件夹
		// lock.lock();
		
		try
		{
			/** 获取当前登录用户信息 **/
			String userCName = AuthorzationUtil.getUserName();
			
			// 获取文件扩展名
			String fileExtension = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww");
			
			// 处理文件存放目录
			String fileDir = null;
			if (folderPath == null || folderPath.trim().length() == 0)
			{
				if (!uploadPrefix.startsWith("/")) uploadPrefix = "/" + uploadPrefix;
				if (!uploadPrefix.endsWith("/"))   uploadPrefix = uploadPrefix + "/";
				fileDir = uploadPrefix;
			}
			else
			{
				if (!folderPath.startsWith("/")) folderPath = "/" + folderPath;
				if (!folderPath.endsWith("/"))	 folderPath = folderPath + "/";
				fileDir = folderPath; 
			}
	
			// 文件分发地址
			String fileurl = null;
			if (fileName == null || "".equals(fileName))
			{
				fileName = MD5.encode(sdf.format(new Date()) + new Random().nextInt(1000)).substring(0, 15).toLowerCase();
				fileExtension = "";
				fileurl = "http://" + domain + fileDir + fileName + fileExtension;
			}
			else
			{
				int lastDotPos = fileName.lastIndexOf(".");
				fileExtension = fileName.substring(lastDotPos).toLowerCase();
				fileurl = "http://" + domain + fileDir + fileName;
			}
			
			/***** 加入允许空文件上传的逻辑 *****/
			if (filedata == null || filedata.length() == 0)
			{
				// 发送一个空文件
				resourceBizService.sendEmptyFiles(fileName, fileurl);
				
				msg = fileurl;
				
				return MSG_PAGE;
			}
			
			
			// 判断文件是否存在
			ResourceFile tmpFile = new ResourceFile();
			tmpFile.setNodeid(nodeId);
			tmpFile.setEnabled(true); // 可用的文件
			tmpFile.setDistributionAddress(fileurl);
			List<ResourceFile> files = fileMapper.getFilesByDistributionAddress(tmpFile);
			if (files != null && files.size() > 0)
			{
				fileExists  = true;
				
				// 暂存已经存在的文件信息
				tmpFile = files.get(0);
				
				if (reupload != 1)
				{
					hasError 	= true;
					msg = "文件" + files.get(0).getFileName() + "已存在";
					
					log.warn(msg); 
					
					return MSG_PAGE;
				}
			}
			
			// 压缩文件
			if(ImageCompressUtil.isNeedCompress(filedata)){
				File destFile = new File( filedata.getAbsolutePath()+".jpg");
				ImageCompressUtil.compressQuality(filedata.getAbsolutePath(), destFile.getAbsolutePath());
				if(filedata.length() > destFile.length()){
					filedata.delete();
					destFile.renameTo(filedata);
				}else{
					destFile.delete();
				}
			}
			
			List<ResourceFolder> folders = new ArrayList<ResourceFolder>();
			ResourceFolder resourceFolder = new ResourceFolder();
			ResourceFile resourceFile = new ResourceFile();
		
			/** 保存File元信息 **/
			resourceFile.setFileName(fileName);
			resourceFile.setFileSize(filedata.length());
			resourceFile.setFileType(fileExtension);
			resourceFolder.setNodeid(nodeId);
			
			String distributionPath = domain + fileDir;
			resourceFolder.setDistributionPath(distributionPath);
			folders = folderMapper.getByDistributionPath(resourceFolder);
			if (folders != null && folders.size() > 0)
			{
				resourceFolder = folders.get(0);
				resourceFile.setParentID(resourceFolder.getFolderID());
			}
			else
			{
				// 判断目录是否存在
				// 此功能已经不再需要了，目前的逻辑是：用户在上传文件的时候，遇到没有的文件夹，就创建；
				// 遇到已经存在的文件夹，就跳过。
				
				// 创建目录
				// /trends/star/detail_2012_12/
				String folderName = fileDir.substring(0, fileDir.lastIndexOf("/"));
				folderName = folderName.substring(folderName.lastIndexOf("/"));
				resourceFolder.setFolderName(folderName);
				resourceFolder.setFolderPath(folderPath);
				
				// 找该目录的父目录
				Integer parentID = resourceBizService.getParentID(userCName, distributionPath, nodeId);
				if (parentID == null)
					resourceFolder.setParentID(0); // 创建父目录为0的新目录
				else
					resourceFolder.setParentID(parentID);
				
				resourceFolder.setDistributionEnabled(true);
				resourceFolder.setDistributionPath(distributionPath);
				resourceFolder.setAuthor(userCName);
				resourceFolder.setNodeid(nodeId);
				resourceFolder.setCreationTime(new Date());
				
				// 文件夹不存在就插入， 存在就跳过
				folders = folderMapper.getByDistributionPath(resourceFolder);
				if (folders == null || folders.size() == 0)
				{
					folderMapper.insert(resourceFolder);
				}
				
				// 取出文件夹，设置文件的parentID
				folders = folderMapper.getByDistributionPath(resourceFolder);
				resourceFolder = folders.get(0);
				resourceFile.setParentID(resourceFolder.getFolderID());
			}
			resourceFile.setAuthor(userCName);
			resourceFile.setDistributionAddress(fileurl);
			resourceFile.setCreationTime(new Date());
			resourceFile.setNodeid(nodeId);
			resourceFile.setEnabled(true);
			
			if (!fileExists)
				fileMapper.insert(resourceFile);
			else // 如果是重传，执行更新操作
			{
				// 修改状态 enabled=true;
				resourceFile.setFileID(tmpFile.getFileID());
				resourceFile.setCreationTime(tmpFile.getCreationTime()); //创建时间保持不变
				resourceFile.setLastModify(new Date());	// 
				fileMapper.update(resourceFile);
			}
			
			/** 分发文件 **/
			
			/*try
			{
				SendFilePlugin sendFile = (SendFilePlugin) pluginFactory.getP("sendFile");
				sendFile.setDir("");
				
				String absolutePath = filedata.getAbsolutePath();
				sendFile.sendFile(absolutePath, fileurl, syncflag);
				msg = fileurl + "|" + filedata.length();
				log.info("file upload & send : " + fileName + " | url : " + fileurl + " | compressedFilesize : " + filedata.length());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				
				hasError = true;
				msg = "分发文件" + resourceFile.getFileName() + "失败： " + ex.getMessage();
				log.error(msg);
				
				// 
				log.info("从数据库删除已经存入的文件信息：" + resourceFile.getDistributionAddress());
				fileMapper.deleteByDistributionAddress(resourceFile);
				
				throw ex;
			}*/
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
			if (msg == null) msg = ex.getMessage();
			hasError = true;
			
			return MSG_PAGE;
		}
		/*finally
		{
			lock.unlock();
		}*/
		
		return MSG_PAGE;
	}
	
	/*
	 * 上传文件的页面
	 */
	public String uploadpage(){
		return "uploadpage";
	}
}
