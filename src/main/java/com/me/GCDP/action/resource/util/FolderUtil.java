package com.me.GCDP.action.resource.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.me.GCDP.model.Resource;
import com.me.GCDP.model.ResourceFolder;

public class FolderUtil
{
	private static final DateFormat DATE_FORMAT  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void copyFields(ResourceFolder folder, Resource resource)
	{
		resource.setId(folder.getFolderID());
		resource.setName(folder.getFolderName());
		resource.setFolderPath(folder.getFolderPath());
		resource.setParentID(folder.getParentID());
		resource.setDistributionEnabled(folder.isDistributionEnabled());
		resource.setDistributionPath(folder.getDistributionPath());
		resource.setAuthor(folder.getAuthor());
		resource.setCreationTimeStr(DATE_FORMAT.format(folder.getCreationTime()));
		resource.setPropertyFlag(false); // 额外加入的标志资源属性的字段 false表示文件夹
	}
	
	public static void copyFieldsToFolder(Resource resource, ResourceFolder folder)
	{
		folder.setFolderID(resource.getId());
		folder.setFolderName(resource.getName());
		folder.setParentID(resource.getParentID());
		folder.setFolderPath(resource.getFolderPath());
		folder.setDistributionEnabled(resource.isDistributionEnabled());
		folder.setDistributionPath(resource.getDistributionPath());
		folder.setAuthor(resource.getAuthor());
		folder.setCreationTime(resource.getCreationTime());
		folder.setNodeid(resource.getNodeid());
	}
	
	public static void copyObjects(List<ResourceFolder> folders, List<Resource> resources)
	{
		Iterator<ResourceFolder> it = folders.iterator();
		while(it.hasNext())
		{
			ResourceFolder folder = it.next();
			Resource resource = new Resource();
			FolderUtil.copyFields(folder, resource);
			resources.add(resource);
		}
	}
	
}
