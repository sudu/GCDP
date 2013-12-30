package com.me.GCDP.action.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.me.GCDP.action.resource.service.ResourceBizService;
import com.me.GCDP.mapper.ResourceFolderMapper;
import com.me.GCDP.model.Resource;
import com.me.GCDP.model.ResourceFolder;
import com.me.GCDP.security.AuthorzationUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.opensymphony.xwork2.ActionSupport;

/*
 * unused class
 * 创建文件夹的动作在创建文件的时候实施
 */
public class FolderMgrAction extends ActionSupport
{
	private static final long	serialVersionUID	= 1L;
	
	private static Log log = LogFactory.getLog(FolderMgrAction.class);
	
	private static final String MSG_PAGE = "msg";
	
	private static final List<String> domains 	= new ArrayList<String>();
	private static String uploadPrefix;

	/** 接收前端的字段 **/
	private Integer	parentID;
	private Integer	nodeId;
	private String	folderName;
	private String	domain;
	private String	folderPath; 
	
	/** 用于返回数据的字串 **/
	private List<Resource> resources;
	
	/** 用于返回结果的字段 **/
	private Boolean hasError = false;
	private String 	msg;
	
	@Autowired
	@Qualifier("folderMapper")
	private ResourceFolderMapper<ResourceFolder> folderMapper;
	
	@Autowired
	@Qualifier("resourceBizService")
	private ResourceBizService resourceBizService;
	
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
	
	public List<Resource> getResources()
	{
		return resources;
	}

	public void setResources(List<Resource> resources)
	{
		this.resources = resources;
	}

	public Integer getParentID()
	{
		return parentID;
	}

	public void setParentID(Integer parentID)
	{
		this.parentID = parentID;
	}

	public Integer getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(Integer nodeId)
	{
		this.nodeId = nodeId;
	}

	public String getFolderName()
	{
		return folderName;
	}

	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
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

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}
	
	public String gotoNewFolder()
	{
		return "gotoNewFolder";
	}

	/*
	 * 输入： nodeId（列表页带入），parentID（列表页带入）， folderName（输入）
	 * 		 folderPath（列表页带入）
	 * 输出： hasError, msg
	 */
	
	private Boolean folderExists = false;
	
	public Boolean getFolderExists()
	{
		return folderExists;
	}

	public void setFolderExists(Boolean folderExists)
	{
		this.folderExists = folderExists;
	}

	public String newFolder()
	{
		hasError = false;
		msg = null;
		
		if (nodeId == null)
		{
			msg = "nodeid为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		if (domain == null)
		{
			msg = "域名为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		if (folderName == null || "".equals(folderName))
		{
			msg = "文件夹名为空";
			hasError = true;
			return MSG_PAGE;
		}
		
		try
		{
			/** 获取当前登录用户信息 **/
			String userCName = AuthorzationUtil.getUserName();
			
			/** 处理上层目录 **/
			if (parentID == null)
			{
				parentID = 0; // 某域名下的顶级目录
			}
			
			/** 处理文件路径 **/
			if (folderPath == null || "".equals(folderPath))
			{
				folderPath = uploadPrefix;
			}
			if (!folderPath.startsWith("/")) folderPath = "/" + folderPath;
			if (folderPath.endsWith("/"))    folderPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
			
			/** 处理域名 **/
			domain = domains.get(Integer.parseInt(domain));
			
			/** 处理文件夹名 **/
			if (!folderName.startsWith("/")) folderName = "/" + folderName;
			if (!folderName.endsWith("/"))   folderName = folderName + "/";
			
			/** 处理分发路径 **/
			String distributionPath = domain + folderPath + folderName;
			
			/** 创建文件夹对象 **/
			ResourceFolder folder = new ResourceFolder();
			folder.setFolderName(folderName);
			folder.setFolderPath(folderPath);
			folder.setParentID(parentID);
			folder.setDistributionEnabled(true);
			folder.setDistributionPath(distributionPath);
			folder.setAuthor(userCName);
			folder.setNodeid(nodeId);
			folder.setCreationTime(new Date());
			
			/** 检查文件夹是否在该域下存在 **/
			if (!resourceBizService.folderExists(distributionPath))
			{
				/** 存文件夹入数据库 **/
				folderMapper.insert(folder);
			}
			else
			{
				folderExists = true;
				hasError = false;
				msg = "此文件夹已存在";
				return MSG_PAGE;
			}
		}
		catch (Exception ex)
		{
			log.error("创建文件夹出错： " + ex);
			hasError = true;
			msg = "创建文件夹出错： " + ex;
			return MSG_PAGE;
		}
		
		return MSG_PAGE;
	}

}
