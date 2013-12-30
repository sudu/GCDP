package com.me.GCDP.action.resource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.me.GCDP.action.resource.service.ResourceBizService;
import com.me.GCDP.action.resource.util.FileUtil;
import com.me.GCDP.action.resource.util.FolderUtil;
import com.me.GCDP.mapper.ResourceFileMapper;
import com.me.GCDP.mapper.ResourceFolderMapper;
import com.me.GCDP.model.PageCondition;
import com.me.GCDP.model.Resource;
import com.me.GCDP.model.ResourceFile;
import com.me.GCDP.model.ResourceFolder;
import com.me.GCDP.util.MD5;
import com.me.GCDP.script.plugin.HttpPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.opensymphony.xwork2.ActionSupport;

public class ResourceMgrAction extends ActionSupport
{
	private static final long serialVersionUID	= -7226803281980638010L;
	
	private static Log log = LogFactory.getLog(ResourceMgrAction.class);
	
	private final static String MSG_PAGE 		 = "msg";
	private final static String FOLDER_LIST_PAGE = "data";
	
	private final static ReentrantLock lock 	 = new ReentrantLock();

	private Integer nodeId;
	
	// 删除
	private String ids;
	private String props;
	
	/** for business **/
	private ResourceFolderMapper<ResourceFolder>	folderMapper;
	private ResourceFileMapper<ResourceFile>		fileMapper;
	
	@Autowired
	@Qualifier("resourceBizService") 
	private ResourceBizService resourceBizService;
	
	/** msg **/
	private String 	msg;
	private boolean hasError;
	
	public String getIds()
	{
		return ids;
	}

	public void setIds(String ids)
	{
		this.ids = ids;
	}

	public String getProps()
	{
		return props;
	}

	public void setProps(String props)
	{
		this.props = props;
	}

	public Integer getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(Integer nodeId)
	{
		this.nodeId = nodeId;
	}

	public ResourceFolderMapper<ResourceFolder> getFolderMapper()
	{
		return folderMapper;
	}

	public void setFolderMapper(
			ResourceFolderMapper<ResourceFolder> folderMapper)
	{
		this.folderMapper = folderMapper;
	}

	public ResourceFileMapper<ResourceFile> getFileMapper()
	{
		return fileMapper;
	}

	public void setFileMapper(ResourceFileMapper<ResourceFile> fileMapper)
	{
		this.fileMapper = fileMapper;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public boolean isHasError()
	{
		return hasError;
	}

	public void setHasError(boolean hasError)
	{
		this.hasError = hasError;
	}
	
	/**
	 * 由首页菜单进入列表页
	 */
	@Override
	public String execute() throws Exception
	{
		return SUCCESS;
	}
	
	/*
	 * 删除文件夹，文件
	 * 输入参数： nodeId，ids（资源ID），props（资源属性标志）
	 * 输出结果： hasError， msg
	 */
	
	// 记录所有要删除的文件
	// (key, JSON)
	private final static ConcurrentHashMap<String, List<ResourceFile>> deletionCache = new ConcurrentHashMap<String, List<ResourceFile>>();
	
	/*private final static Object deletionLock = new Object();*/
	
	private String deletionKey;
	
	public String getDeletionKey()
	{
		return deletionKey;
	}

	public void setDeletionKey(String deletionKey)
	{
		this.deletionKey = deletionKey;
	}

	public String delete()
	{
		// 删除功能：1.删除文件夹 -- 删除该文件夹下的所有文件及文件夹；2.删除文件
		
		hasError = false;
		msg = null;
		
		if (nodeId == null)
		{
			msg = "nodeid为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		lock.lock();
		
		ExecutorService threadPool = null;
		try
		{
			// 处理页面参数
			String[] resourceIDs = ids.split(",");    
			String[] resourceFlags = props.split(",");
			
			// 异步处理
			threadPool = Executors.newFixedThreadPool(2*Runtime.getRuntime().availableProcessors());  
			CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(threadPool);
			
			// 为每次删除操作产生一个唯一标识
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_ww");
			deletionKey = MD5.encode(sdf.format(new Date()) + new Random().nextInt(1000));
			
			/** 查出所有的要删除的资源（文件及文件夹） **/
			List<ResourceFile> toBedeletedFilesAll = new ArrayList<ResourceFile>();
			List<ResourceFile> toBedeletedFiles = new ArrayList<ResourceFile>();
			Set<ResourceFolder> toBeDeletedFolders = new HashSet<ResourceFolder>();
			for (int index = 0; index < resourceIDs.length; index++)
			{
				int resourceID = Integer.parseInt(resourceIDs[index]);
				boolean resourceFlag = Boolean.parseBoolean(resourceFlags[index]);
				
				// 如果是文件夹，就把文件夹的路径存入到集合中
				if (!resourceFlag)
				{
					resourceBizService.collectFolderPaths(resourceID, nodeId, resourceFlag, toBeDeletedFolders);
				}
				else
				{
					toBedeletedFiles = resourceBizService.fetchAllFileResources(resourceID, nodeId, resourceFlag);
					toBedeletedFilesAll.addAll(toBedeletedFiles);
					toBedeletedFiles.clear();
				}
			}
			
			// 遍历所有文件夹
			toBedeletedFiles = resourceBizService.collectAllFiles(toBeDeletedFolders);
			toBedeletedFilesAll.addAll(toBedeletedFiles);
			
			// 把所有待删除的文件放入缓存中
			deletionCache.put(deletionKey, toBedeletedFilesAll);
			
			/** 删除资源 **/
			// 提交任务
			for (int index = 0; index < resourceIDs.length; index++)
			{
				int resourceID = Integer.parseInt(resourceIDs[index]);
				boolean resourceFlag = Boolean.parseBoolean(resourceFlags[index]);
				completionService.submit(new DeletionExecutor(resourceID, resourceFlag, nodeId, deletionKey));
			}
		}
		catch (Exception ex)
		{
			log.error("删除出错：" + ex);
			msg = "删除出错：" + ex;
			hasError = true;
			return MSG_PAGE;
		}
		finally
		{
			if (threadPool != null)
				threadPool.shutdown();
			
			lock.unlock();
		}
		
		return MSG_PAGE;
	}
	
	class DeletionExecutor implements Callable<Integer>
	{
		private int		resourceID;
		private boolean	resourceFlag;
		private Integer	nodeId;
		private String  deletionKey;

		public DeletionExecutor(int resourceID, boolean resourceFlag, Integer nodeId, String deletionKey)
		{
			super();
			this.resourceID 	= resourceID;
			this.resourceFlag 	= resourceFlag;
			this.nodeId 		= nodeId;
			this.deletionKey 	= deletionKey;
		}

		@Override
		public Integer call() throws Exception
		{
			/** 
			   加入锁， 同步代码块。 避免多线程执行的时候，删除不同步的问题。
			 **/
			
			synchronized (this) 
			{
				if (!resourceFlag) // 删除文件夹 ， 递归删除
				{
					// 删除文件夹，文件， 覆盖已分发的文件
					resourceBizService.deleteChildrenItems(resourceID, nodeId, deletionKey, deletionCache);
				}
				else
				{
					ResourceFile file = new ResourceFile();
					file = new ResourceFile();
					file.setFileID(resourceID);
					file.setNodeid(nodeId);
					file.setEnabled(false); // 该记录处于删除状态
					file.setLastModify(new Date());
					fileMapper.disablbeRecord(file);
					
					// 发送空文件,覆盖已分发的文件
					List<ResourceFile> files = fileMapper.getFilesByIdAndNode(file);
					file = files.get(0);
					resourceBizService.sendEmptyFiles(file.getFileName(), file.getDistributionAddress());
					
					// 从缓存取出要删除的文件信息
					List<ResourceFile> toBeDeletedObjs = deletionCache.get(deletionKey); 
					
					// 删除文件
					Iterator<ResourceFile> it4TmpFiles = toBeDeletedObjs.iterator();
					while (it4TmpFiles.hasNext())
					{
						ResourceFile tmpFile = it4TmpFiles.next();
						if (tmpFile.getFileID().equals(file.getFileID()))
						{
							it4TmpFiles.remove();
							break;
						}
					}
					
					// 修改缓存的状态
					deletionCache.put(deletionKey, toBeDeletedObjs); // 缓存中相应减一
				}
				
				// 每次执行任务后，判断要删除的文件是否为空，如果为空，则从缓存删除之
				List<ResourceFile> undeletedFiles = deletionCache.get(deletionKey); 
				if (undeletedFiles == null || undeletedFiles.size() == 0) 
				{
					deletionCache.remove(deletionKey);
				}
			}
			
			// 返回值无意义
			return null; 
		}
	}
	
	/*
	 * 获取还没有删除的文件集合， JSON字串形式返回
	 */
	
	/** 返回字段 **/
	private List<ResourceFile> undeletedFiles;

	public List<ResourceFile> getUndeletedFiles()
	{
		return undeletedFiles;
	}

	public void setUndeletedFiles(List<ResourceFile> undeletedFiles)
	{
		this.undeletedFiles = undeletedFiles;
	}

	public String fetchUndeletedFiles()
	{
		hasError = false;
		msg = null;
		
		if (nodeId == null)
		{
			msg = "nodeId为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		try
		{
			undeletedFiles = deletionCache.get(deletionKey);
		}
		catch (Exception ex)
		{
			log.error("检查删除状态出错： " + ex);
			msg = "检查删除状态出错： " + ex;
			hasError = true;
		}
		
		return MSG_PAGE;
	}
	
	/*
	 * 列出所有的文件夹（分页）
	 * 
	 * 搜索所有文件夹（按folderPath，即文件夹路径）
	 * 输入参数： nodeId， start，limit
	 * 输出结果： totalCount， data
	 */
	
	/** 输入参数 **/
	private int 	start = 0;
	private Integer limit;
	private String  folderPath;
	
	/** 返回数据 **/
	private int 	totalCount;
	private String 	data;
	
	public int getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(int totalCount)
	{
		this.totalCount = totalCount;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public int getStart()
	{
		return start;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public int getLimit()
	{ 
		return limit;
	}

	public void setLimit(Integer limit)
	{
		this.limit = limit;
	}

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		// 处理汉字乱码
		folderPath = filterCoding(folderPath);
		this.folderPath = folderPath;
	}

	public String listFolders()
	{
		hasError = false;
		msg = null;
		
		if (nodeId == null)
		{
			msg = "nodeid为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		try
		{
			List<ResourceFolder> folders = null;
			
			PageCondition pc = new PageCondition();
			if (limit != null)
			{
				pc.setFrom(start);
				pc.setLimit(limit);
				pc.setNodeId(nodeId);
				pc.setFilterTxt("folderPath"); // 用于搜索的字段， 可以为空
				pc.setFilterValue(folderPath); // 不论是否为空都可以
				folders = folderMapper.getPagenationFolders(pc);
			}
			else
			{
				pc.setNodeId(nodeId);
				pc.setFilterTxt("folderPath"); // 用于搜索的字段， 可以为空
				pc.setFilterValue(folderPath); // 不论是否为空都可以
				folders = folderMapper.getPagenationFoldersWithoutLimit(pc);
			}
			
			// 加工日期格式，加入
			List<Resource> resources = new ArrayList<Resource>(folders.size());
			FolderUtil.copyObjects(folders, resources);
			
			totalCount  = folderMapper.getFolderCount(pc);
			data = JSONArray.fromObject(resources).toString();
		}
		catch (Exception ex)
		{
			log.error("获取文件夹列表出错：" + ex);
			msg = "获取文件夹列表出错：" + ex;
			hasError = true;
			return MSG_PAGE;
		}
		
		return FOLDER_LIST_PAGE;
	}
	
	/*
	 * 文件列表（支持分页）
	 * 输入参数： start，limit，nodeId， folderID
	 * 输出结果： totalCount，data
	 */
	
	private String  fileName = null;
	
	private Integer folderID;
	
	public Integer getFolderID()
	{
		return folderID;
	}

	public void setFolderID(Integer folderID)
	{
		this.folderID = folderID;
	}
	
	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		fileName = filterCoding(fileName);
		this.fileName = fileName;
	}
	
	public String listFiles()
	{
		hasError = false;
		msg = null;
		
		try
		{
			List<ResourceFile> files = null;
			
			PageCondition pc = new PageCondition();
			if (limit != null)
			{
				pc.setFrom(start);
				pc.setLimit(limit);
				pc.setParentID(folderID);
				pc.setNodeId(nodeId);
				pc.setEnabled(true);
				pc.setFilterTxt("fileName"); // 用于搜索的字段
				pc.setFilterValue(fileName); // 不论是否为空都可以
				files = fileMapper.getPagenationFiles(pc);
			}
			else
			{
				pc.setParentID(folderID);
				pc.setNodeId(nodeId);
				pc.setEnabled(true);
				pc.setFilterTxt("fileName"); // 用于搜索的字段
				pc.setFilterValue(fileName); // 不论是否为空都可以
				files = fileMapper.getPagenationFilesWithoutLimit(pc);
			}
			
			List<Resource> resources = new ArrayList<Resource>(files.size());
			FileUtil.copyObjects(files, resources);
			
			totalCount  = fileMapper.getFileCount(pc);  
			data = JSONArray.fromObject(resources).toString();
		}
		catch (Exception ex)
		{
			log.error("获取文件列表出错：" + ex);
			msg = "获取文件列表出错：" + ex;
			hasError = true;
			return MSG_PAGE;
		}
		
		return FOLDER_LIST_PAGE;
	}
	
	private String filterCoding(String source)
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
	
	/*
	 * 编辑文件内容， 文件的后缀为 CSS，TXT，JS， HTML， HTM
	 */
	
	private String distributionAddress;
	private String fileContent;
	
	@Autowired
	@Qualifier("pluginFactory") 
	private ScriptPluginFactory pluginFactory;
	
	public String getDistributionAddress()
	{
		return distributionAddress;
	}

	public void setDistributionAddress(String distributionAddress)
	{
		this.distributionAddress = distributionAddress;
	}

	public String getFileContent()
	{
		return fileContent;
	}

	public void setFileContent(String fileContent)
	{
		this.fileContent = fileContent;
	}

	public String editFileContent()
	{
		hasError = false;
		msg = null;
		
		String fileContent = null; // 存放文件内容
		
		try
		{
			// 获取文件内容
			HttpPlugin httpPlugin = (HttpPlugin) pluginFactory.getP("http");
			fileContent = httpPlugin.sendGet(distributionAddress, "", 5000, 5000);
			this.fileContent = fileContent;
		}
		catch(Exception ex)
		{
			hasError = true;
			msg = "获取文件内容出错： " + ex.getMessage();
			return MSG_PAGE;
		}
		
		msg = fileContent;
		
		return MSG_PAGE;
	}
	
	private Integer fileID;   // 文件ID
	private String  fileDesc; // 文件描述
	
	public Integer getFileID()
	{
		return fileID;
	}

	public void setFileID(Integer fileID)
	{
		this.fileID = fileID;
	}

	public String getFileDesc()
	{
		return fileDesc;
	}

	public void setFileDesc(String fileDesc)
	{
		this.fileDesc = fileDesc;
	}

	public String saveSend()
	{
		hasError = false;
		msg = null;
		
		if (nodeId == null)
		{
			msg = "nodeid为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		try
		{
			// 文件记录到数据库
			ResourceFile file = new ResourceFile();
			file.setFileID(fileID);
			file.setFileDesc(fileDesc);
			fileMapper.updateFileDesc(file);
			
			/**** 分发文件 ****/
			// 写入临时文件
			String tmpFileDir = resourceBizService.getEmptyTmpFileDir();
			File tmpDir = new File(tmpFileDir);
			if (!tmpDir.exists()) tmpDir.mkdirs();
			
			String tmpFileName = distributionAddress.substring(distributionAddress.lastIndexOf("/"));
			File fileData = new File(tmpDir.getPath(), tmpFileName);
			if (!fileData.exists()) fileData.createNewFile();
			
			FileUtil.writeContent(fileData, fileContent);
			
			/*// 分发临时文件
			String fileurl = distributionAddress;
			SendFilePlugin sendFile = (SendFilePlugin) pluginFactory.getP("sendFile");
			sendFile.setDir("");
			Map<String, String> results = sendFile.sendFile(fileData.getAbsolutePath(), fileurl, "1");
			
			// 删除临时文件
			if ("0".equals(results.get("ret")))
				fileData.delete();*/
		}
		catch (Exception ex)
		{
			hasError = true;
			msg = "更新文件描述或内容失败： " + ex.getMessage();
			return MSG_PAGE;
		}
		
		return MSG_PAGE;
	}

}
