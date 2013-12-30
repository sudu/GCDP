package com.me.GCDP.action.resource.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.me.GCDP.model.Resource;
import com.me.GCDP.model.ResourceFile;

public class FileUtil
{
	
	private static final DateFormat DATE_FORMAT  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void copyFields(ResourceFile file, Resource resource)
	{
		resource.setId(file.getFileID());
		resource.setName(file.getFileName());
		resource.setDesc(file.getFileDesc());
		resource.setSize(file.getFileSize());
		resource.setType(file.getFileType());
		resource.setParentID(file.getParentID());
		resource.setDistributionAddress(file.getDistributionAddress());
		resource.setAuthor(file.getAuthor());
		// resource.setCreationTime(file.getCreationTime());
		resource.setCreationTimeStr(DATE_FORMAT.format(file.getCreationTime()));
		resource.setPropertyFlag(true); // 额外加入的标志资源属性的字段 false表示文件夹
	}
	
	public static void copyFieldsToFile(Resource resource, ResourceFile file)
	{
		file.setFileID(resource.getId());
		file.setFileName(resource.getName());
		file.setFileDesc(resource.getDesc());
		file.setParentID(resource.getParentID());
		file.setDistributionAddress(resource.getDistributionAddress());
		file.setAuthor(resource.getAuthor());
		file.setCreationTime(resource.getCreationTime());
		file.setNodeid(resource.getNodeid());
	}
	
	public static void copyObjects(List<ResourceFile> files, List<Resource> resources)
	{
		Iterator<ResourceFile> it = files.iterator();
		while(it.hasNext())
		{
			ResourceFile folder = it.next();
			Resource resource = new Resource();
			FileUtil.copyFields(folder, resource);
			resources.add(resource);
		}
	}
	
	public static void writeContent(File fileData, String fileContent) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(fileData);
		fos.write(fileContent.getBytes());
		fos.close();
		fos.flush();
	}
	
}
