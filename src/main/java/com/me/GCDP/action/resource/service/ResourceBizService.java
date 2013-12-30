package com.me.GCDP.action.resource.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.ResourceFileMapper;
import com.me.GCDP.mapper.ResourceFolderMapper;
import com.me.GCDP.model.ResourceFile;
import com.me.GCDP.model.ResourceFolder;
import com.me.GCDP.script.plugin.ScriptPluginFactory;

public class ResourceBizService
{
	private static Log log = LogFactory.getLog(ResourceBizService.class);
	
	private String 									emptyTmpFileDir; // 空文件默认目录
	private ResourceFolderMapper<ResourceFolder> 	folderMapper;
	private ResourceFileMapper<ResourceFile>	 	fileMapper;
	private ScriptPluginFactory                		pluginFactory;

	public void setEmptyTmpFileDir(String emptyTmpFileDir)
	{
		this.emptyTmpFileDir = emptyTmpFileDir;
	}

	public String getEmptyTmpFileDir()
	{
		return emptyTmpFileDir;
	}

	public void setFolderMapper(ResourceFolderMapper<ResourceFolder> folderMapper)
	{
		this.folderMapper = folderMapper;
	}
	
	public void setFileMapper(ResourceFileMapper<ResourceFile> fileMapper)
	{
		this.fileMapper = fileMapper;
	}

	public void setPluginFactory(ScriptPluginFactory pluginFactory)
	{
		this.pluginFactory = pluginFactory;
	}

	public synchronized void deleteChildrenItems(Integer resourceID, Integer nodeId, String deletionKey, Map<String, List<ResourceFile>> deletionCache) throws Exception
	{
		try
		{
			// 查找该目录目录
			ResourceFolder folder = new ResourceFolder();
			folder.setFolderID(resourceID);
			folder.setNodeid(nodeId);
			List<ResourceFolder> folders = folderMapper.get(folder);
			
			ResourceFolder parentFolder = null;
			
			// 查找所有子目录
			if (folders != null && folders.size() > 0)
			{
				folder 		 = folders.get(0);
				parentFolder = folder;
				folders 	 = folderMapper.getByDistributionPathLike(folder);
				Iterator<ResourceFolder> it4Folder = folders.iterator();
				while (it4Folder.hasNext())
				{
					folder = it4Folder.next();
					deleteFiles(folder, deletionKey, deletionCache);
					folderMapper.delete(folder);
					
					log.info("folder " + folder.getFolderPath() + " deleted");
				}
				
				deleteFiles(parentFolder, deletionKey, deletionCache); // 删除文件夹下的文件
				folderMapper.delete(parentFolder); // 在此处删除文件夹
				
				log.info("folder " + parentFolder.getFolderPath() + " deleted");
			}
		}
		catch (Exception ex)
		{
			log.error("删除文件夹 " + resourceID + " 下的子文件夹及文件出错： " + ex);
			throw ex;
		}
	}
	
	private void deleteFiles(ResourceFolder folder, String deletionKey, Map<String, List<ResourceFile>> deletionCache) throws Exception
	{
 		Integer folderID = folder.getFolderID();
		
		log.info("deleting files under folder " + folder.getFolderPath());
		
		try
		{
			ResourceFile file = new ResourceFile();
			file.setParentID(folder.getFolderID());
			file.setNodeid(folder.getNodeid());  
			file.setEnabled(false); // 该记录处于删除状态
			file.setLastModify(new Date());
			
			// 修改数据库记录的状态
			fileMapper.disablbeRecordByParentID(file);
			
			// file.setEnabled(true);
			
			// 覆盖服务器上已经分发的同名文件
			List<ResourceFile> toBeDeletedFilesInDB  = fileMapper.getFilesByParentAndNode(file);
			Iterator<ResourceFile> it4DeletedFilesDB = toBeDeletedFilesInDB.iterator();
			while (it4DeletedFilesDB.hasNext())
			{
				file = it4DeletedFilesDB.next();
				
				// 向服务器发送空文件
				sendEmptyFiles(file.getFileName(), file.getDistributionAddress());
				
				// 取出删除文件缓存
				List<ResourceFile> toBeDeletedFiles = deletionCache.get(deletionKey);
				if (toBeDeletedFiles == null || toBeDeletedFiles.size() == 0) continue;
				
				// 删除文件
				Iterator<ResourceFile> it4TmpFiles = toBeDeletedFiles.iterator();
				while (it4TmpFiles.hasNext())
				{
					ResourceFile tmpFile = it4TmpFiles.next();
					if (tmpFile.getFileID().equals(file.getFileID()))
					{
						it4TmpFiles.remove();
						break;
					}
				}
				
				// 更新删除文件缓存
				deletionCache.put(deletionKey, toBeDeletedFiles); // 缓存中相应减一
			}
		}
		catch (Exception ex)
		{
			log.error("删除文件夹" + folderID + "下的文件出错： " + ex);
			throw ex;
		}
	}
	
	public void sendEmptyFiles(String fileName, String distributionAddress) throws Exception
	{
		/*try
		{
			File filedata = new File(emptyTmpFileDir);
			if (!filedata.exists()) filedata.mkdirs();
			
			filedata = new File(filedata.getPath(), fileName);
			if (!filedata.exists()) filedata.createNewFile();
			
			String fileurl = distributionAddress;
			
			SendFilePlugin sendFile = (SendFilePlugin) pluginFactory.getP("sendFile");
			sendFile.setDir("");
			Map<String, String> results = sendFile.sendFile(filedata.getAbsolutePath(), fileurl, "1");
			
			if ("0".equals(results.get("ret")))
				filedata.delete();
			
		}
		catch (Exception ex)
		{
			log.error("分发空文件覆盖已分发过的文件出错： " + ex);
			throw ex;
		}*/
	}
	
	/*
	 * 根据分发的域名判断是文件夹是否存在
	 */
	public boolean folderExists(String distributionPath) throws Exception
	{
		boolean ret = false;
		
		try 
		{

			ResourceFolder folder = new ResourceFolder();
			folder.setDistributionPath(distributionPath);
			
			List<ResourceFolder> folders = folderMapper.getByDistributionPath(folder);
			if (folders == null || folders.size() == 0)
			{}
			else
			{
				ret = true;
			}
		}
		catch (Exception ex)
		{
			log.error("根据分发目录获取文件夹出错：" + ex);
			throw ex;
		}
		
		return ret;
	}
	
	/*
	 * 查找文件夹的父目录
	 */
	public synchronized Integer getParentID(String author, String distributionPath, Integer nodeId) throws Exception
	{
		// y1.ifengimg.com/a/2010/0601/aaa/
		
		Integer parentID = null;
		
		String[] dirs = distributionPath.split("/"); 
		
		ResourceFolder folder = new ResourceFolder();
		folder.setNodeid(nodeId);
		folder.setDistributionPath(distributionPath);
		List<ResourceFolder> folders = folderMapper.getByDistributionPath(folder);
		// 存在
		if (folders != null && folders.size() > 0)
		{
			folder = folders.get(0);
			parentID = folder.getParentID();
		}
		// 不存在
		else
		{
			// 逐级向下搜索
			String tmpPath = "";
			ResourceFolder tmpFolder = null;
			for (int index = 0; index < dirs.length /*- 1*/; index++)
			{
				if (index == 0)
				{
					tmpPath = tmpPath + dirs[index];
					continue;
				}
				else
				{
					if (tmpPath.contains("/"))
					{
						tmpPath = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
					}
					tmpPath = tmpPath + "/" + dirs[index] + "/";
				}
				
				// 检查目录是否存在
				tmpFolder = new ResourceFolder();
				tmpFolder.setNodeid(nodeId);
				tmpFolder.setDistributionPath(tmpPath);
				List<ResourceFolder> tmpFolders = folderMapper.getByDistributionPath(tmpFolder);
				if (tmpFolders != null && tmpFolders.size() > 0)
				{
					if (index != (dirs.length - 1))
					{
						continue;
					}
					else
					{
						// 取回dirs.length - 1的上一级目录
						ResourceFolder lastLevelFolder = new ResourceFolder();
						lastLevelFolder.setNodeid(nodeId);
						
						// 获取上一级目录
						int i = 0;
						while (i <= 1)
						{
							tmpPath = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
							i++;
						}
						lastLevelFolder.setDistributionPath(tmpPath);
						
						List<ResourceFolder> parentFolders = folderMapper.getByDistributionPath(lastLevelFolder);
						lastLevelFolder = parentFolders.get(0);
						parentID        = lastLevelFolder.getFolderID();
					}
				}
				else
				{
					// if (index == 0) continue; // 走不到，删除之
					// 创建目录
					// 某域名下的第一级目录，设置parentID=0
					if (index == 1) 
					{
						tmpFolder.setFolderName(dirs[index]);
						tmpFolder.setAuthor(author);
						String folderPath = "/" + dirs[index] + "/";
						tmpFolder.setFolderPath(folderPath);
						tmpFolder.setParentID(0);
					}
					else
					{
						// 文件名
						String folderName = "";
						folderName = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
						folderName = folderName.substring(folderName.lastIndexOf("/"));
						folderName = folderName.replace("/", "");
						tmpFolder.setFolderName(folderName);
						
						// 文件夹
						String folderPath = tmpPath.substring(tmpPath.indexOf("/"));
						tmpFolder.setFolderPath(folderPath);
						
						// parentID 查找上一级的目录ID
						String parentDistributionPath = tmpPath.substring(0, tmpPath.lastIndexOf("/"));
						parentDistributionPath = parentDistributionPath.substring(0, parentDistributionPath.lastIndexOf("/"));
						if (!parentDistributionPath.endsWith("/")) parentDistributionPath += "/"; 
						ResourceFolder lastLevelFolder = new ResourceFolder();
						lastLevelFolder.setNodeid(nodeId);
						lastLevelFolder.setDistributionPath(parentDistributionPath);
						List<ResourceFolder> parentFolders = folderMapper.getByDistributionPath(lastLevelFolder);
						lastLevelFolder = parentFolders.get(0);
						parentID        = lastLevelFolder.getFolderID();
						tmpFolder.setParentID(parentID);
					}
					tmpFolder.setAuthor(author);
					tmpFolder.setCreationTime(new Date());
					tmpFolder.setDistributionEnabled(true);
					tmpFolder.setDistributionPath(tmpPath);
					folderMapper.insert(tmpFolder);
				}
			}
		}
		
		return parentID;
	}
	
	/*
	 * 获取所有的要删除的资源（文件）
	 */ 
	@Deprecated
	public List<ResourceFile> fetchAllResources(Integer resourceID, Integer nodeId, boolean resourceFlag)
	{
		List<ResourceFile> toBeDeletedObjs = new ArrayList<ResourceFile>();
		
		if (!resourceFlag) // 查找要删除的所有文件夹
		{
			ResourceFolder folder = new ResourceFolder();
			folder.setFolderID(resourceID);
			folder.setNodeid(nodeId);
			List<ResourceFolder> folders = folderMapper.get(folder);
			if (folders != null && folders.size() > 0)
			{
				folder = folders.get(0);
				
				// 查找文件夹下的文件
				ResourceFile file = new ResourceFile();
				file.setEnabled(true);
				file.setParentID(folder.getFolderID());
				file.setNodeid(nodeId);
				List<ResourceFile> childFiles = fileMapper.getFilesByRecordStatus(file);
				toBeDeletedObjs.addAll(childFiles);
				
				// 查找子文件夹下的文件
				folders = folderMapper.getByDistributionPathLike(folder);
				Iterator<ResourceFolder> it4Folder = folders.iterator();
				while (it4Folder.hasNext())
				{
					folder = it4Folder.next();
					// 查找其下所有的文件， 放入缓存
					file = new ResourceFile();
					file.setParentID(folder.getFolderID());
					file.setNodeid(folder.getNodeid());
					file.setEnabled(true); 
					List<ResourceFile> toBeDeletedFiles = fileMapper.getFilesByRecordStatus(file); 
					toBeDeletedObjs.addAll(toBeDeletedFiles);
				}
			}
		}
		else
		{
			ResourceFile file = new ResourceFile();
			file.setFileID(resourceID);
			file.setNodeid(nodeId);
			file.setEnabled(true); 
			List<ResourceFile> files = fileMapper.get(file);
			toBeDeletedObjs.add(files.get(0));
		}
		
		return toBeDeletedObjs;
	}
	
	public void collectFolderPaths(Integer resourceID, Integer nodeId, boolean resourceFlag, Set<ResourceFolder> folderPaths)
	{
		if (!resourceFlag) // 查找要删除的所有文件夹
		{
			ResourceFolder folder = new ResourceFolder();
			folder.setFolderID(resourceID);
			folder.setNodeid(nodeId);
			List<ResourceFolder> folders = folderMapper.get(folder);
			if (folders != null && folders.size() > 0)
			{
				folder = folders.get(0);
				folderPaths.add(folder); 
			}
		}
	}
	
	public List<ResourceFile> collectAllFiles(Set<ResourceFolder> toBeDeletedFolders)
	{
		List<ResourceFile> toBeDeletedFiles = new ArrayList<ResourceFile>();
		
		Iterator<ResourceFolder> it4ToBeDeletedFolders = toBeDeletedFolders.iterator();
		while (it4ToBeDeletedFolders.hasNext())
		{
			ResourceFolder folder = it4ToBeDeletedFolders.next();
			
			ResourceFile file = new ResourceFile();
			file.setParentID(folder.getFolderID());
			file.setNodeid(folder.getNodeid());
			file.setEnabled(true); 
			List<ResourceFile> files = fileMapper.getFilesByRecordStatus(file); 
			
			toBeDeletedFiles.addAll(files);
		}
		
		return toBeDeletedFiles;
	}
	
	public List<ResourceFile> fetchAllFileResources(Integer resourceID, Integer nodeId, boolean resourceFlag)
	{
		List<ResourceFile> toBedeletedFiles = new ArrayList<ResourceFile>();
		ResourceFile file = new ResourceFile();
		file.setFileID(resourceID);
		file.setNodeid(nodeId);
		file.setEnabled(true); 
		List<ResourceFile> files = fileMapper.get(file);
		toBedeletedFiles.add(files.get(0));
		return toBedeletedFiles;
	}
	
	
	
}

