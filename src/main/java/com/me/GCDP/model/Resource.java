package com.me.GCDP.model;

import java.util.Date;

public class Resource extends BaseModel
{
	private static final long	serialVersionUID	= 1L;

	private Integer id;
	
	private String  name;
	
	private String  desc;
	
	private double  size;
	
	private String  type;
	
	private Integer parentID;
	private String  parentPath;
	
	private String  folderPath;
	
	private String  distributionAddress;
	
	// private String  domain;
	
	private String  distributionPath;
	
	private String  author;
	
	private Date    creationTime;
	
	private String  creationTimeStr;
	
	/** 
	 	标识一个资源是文件夹还是文件， 对于业务模块和展示模块有用 
	 	展示模块（前端）可以根据此判断资源的属性，选择不同展示图标
	 	propertyFlag=false表示文件夹；true表示文件
	 	此属性不入库
	 **/
	private boolean propertyFlag;				
	
	/** folder fileds **/
	
	private boolean	distributionEnabled; 	// folder
	
	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getSize()
	{
		return size;
	}

	public void setSize(double size)
	{
		this.size = size;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Integer getParentID()
	{
		return parentID;
	}

	public void setParentID(Integer parentID)
	{
		this.parentID = parentID;
	}
/*
	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}*/

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public Date getCreationTime()
	{
		return creationTime;
	}

	public void setCreationTime(Date creationTime)
	{
		this.creationTime = creationTime;
	}

	public boolean isDistributionEnabled()
	{
		return distributionEnabled;
	}

	public void setDistributionEnabled(boolean distributionEnabled)
	{
		this.distributionEnabled = distributionEnabled;
	}

	public boolean isPropertyFlag()
	{
		return propertyFlag;
	}

	public void setPropertyFlag(boolean propertyFlag)
	{
		this.propertyFlag = propertyFlag;
	}

	public String getCreationTimeStr()
	{
		return creationTimeStr;
	}

	public void setCreationTimeStr(String creationTimeStr)
	{
		this.creationTimeStr = creationTimeStr;
	}

	public String getParentPath()
	{
		return parentPath;
	}

	public void setParentPath(String parentPath)
	{
		this.parentPath = parentPath;
	}

	public String getDistributionAddress()
	{
		return distributionAddress;
	}

	public void setDistributionAddress(String distributionAddress)
	{
		this.distributionAddress = distributionAddress;
	}

	public String getDistributionPath()
	{
		return distributionPath;
	}

	public void setDistributionPath(String distributionPath)
	{
		this.distributionPath = distributionPath;
	}

}
