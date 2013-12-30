/**
 * 
 */
package com.me.GCDP.model;


/**
 * @author zhangzy mybatis参数类 2011-7-5
 */
public class PageCondition
{
	private int		limit		= 10;
	private int		id;
	private int		from;
	private int		groupId;
	private int		userId;
	private int		pageSize;
	private String	filterTxt;
	private String	filterValue;
	private int		nodeId;

	private String	username	= null;
	private String	key			= null;

	// 记录是否失效
	// added by HANXAINQI
	// 用于资源管理
	private boolean	enabled;
	// 标记文件的父文件夹
	private int 	parentID;

	public int getParentID()
	{
		return parentID;
	}

	public void setParentID(int parentID)
	{
		this.parentID = parentID;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getNodeId()
	{
		return nodeId;
	}

	public void setNodeId(int nodeId)
	{
		this.nodeId = nodeId;
	}

	public String getFilterTxt()
	{
		return filterTxt;
	}

	public void setFilterTxt(String filterTxt)
	{
		this.filterTxt = filterTxt;
	}

	public String getFilterValue()
	{
		return filterValue;
	}

	public void setFilterValue(String filterValue)
	{
		this.filterValue = filterValue;
	}

	public int getFrom()
	{
		return from;
	}

	public void setFrom(int from)
	{
		this.from = from;
	}

	public int getGroupId()
	{
		return groupId;
	}

	public void setGroupId(int groupId)
	{
		this.groupId = groupId;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

}
