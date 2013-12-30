package com.me.GCDP.model;

import java.util.Date;

public class ResourceFile extends BaseModel
{
	private static final long	serialVersionUID	= 1125547018077972432L;
	
	/** file resource's unique ID **/
	private Integer fileID;
	
	/** file's name **/
	private String  fileName;
	
	private String  fileDesc;
	
	/** file's size **/
	private double  fileSize;
	
	/** file's type **/
	private String  fileType;
	
	/** file's parent folder ID **/
	private Integer parentID;
	
	/** the address where the file will be distributed **/
	private String  distributionAddress;
	
	/** the author who creates the file **/
	private String  author;
	
	/** time when the file is created **/
	
	private Date    creationTime;
	
	private boolean enabled;
	
	private Date    lastModify;
	
	public Date getLastModify()
	{
		return lastModify;
	}

	public void setLastModify(Date lastModify)
	{
		this.lastModify = lastModify;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Integer getFileID()
	{
		return fileID;
	}

	public void setFileID(Integer fileID)
	{
		this.fileID = fileID;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileDesc()
	{
		return fileDesc;
	}

	public void setFileDesc(String fileDesc)
	{
		this.fileDesc = fileDesc;
	}

	public double getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(double fileSize)
	{
		this.fileSize = fileSize;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public Integer getParentID()
	{
		return parentID;
	}

	public void setParentID(Integer parentID)
	{
		this.parentID = parentID;
	}

	public String getDistributionAddress()
	{
		return distributionAddress;
	}

	public void setDistributionAddress(String distributionAddress)
	{
		this.distributionAddress = distributionAddress;
	}

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

}
