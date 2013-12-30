package com.me.GCDP.model;

import java.util.Date;

public class ResourceFolder extends BaseModel
{
	private static final long	serialVersionUID	= -1501681836506892917L;

	/** folder unique ID **/
	private Integer	folderID;

	/** the name of the folder you operate on **/
	private String	folderName;

	/** the path where the folder exists **/
	private String	folderPath;

	/** the unique ID of folder's parent **/
	private Integer	parentID;

	/** flag indicate whether to distribute its child 
	 * folders or files **/
	private boolean	distributionEnabled;

	/** if 'distributionEnabled' is true, children 
	 * will be distributed into this address **/
	// private String	distributionAddress;
	
	private String	distributionPath;

	/** the folder's creator **/
	private String	author;

	/** the folder's creation time **/
	private Date	creationTime;

	/** the node ID that this folder is corresponding to **/
	/*private Integer	nodeID;*/
	
	// private boolean propertyFlag;

	public Integer getFolderID()
	{
		return folderID;
	}

	public void setFolderID(Integer folderID)
	{
		this.folderID = folderID;
	}

	public String getFolderName()
	{
		return folderName;
	}

	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
	}

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public Integer getParentID()
	{
		return parentID;
	}

	public void setParentID(Integer parentID)
	{
		this.parentID = parentID;
	}

	public boolean isDistributionEnabled()
	{
		return distributionEnabled;
	}

	public void setDistributionEnabled(boolean distributionEnabled)
	{
		this.distributionEnabled = distributionEnabled;
	}

	/*public String getDistributionAddress()
	{
		return distributionAddress;
	}

	public void setDistributionAddress(String distributionAddress)
	{
		this.distributionAddress = distributionAddress;
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

	public String getDistributionPath()
	{
		return distributionPath;
	}

	public void setDistributionPath(String distributionPath)
	{
		this.distributionPath = distributionPath;
	}

}
