/**
 * 
 */
package com.me.GCDP.version;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.mapper.VersionMapper;
import com.me.GCDP.model.Version;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.script.plugin.CmppDBPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
/**
 * @author chengds
 *
 */
public class VersionHelper2 {
	private static Log log = LogFactory.getLog(VersionHelper2.class);
	private ScriptPluginFactory scriptPluginFactory = (ScriptPluginFactory)SpringContextUtil.getBean("pluginFactory");
	
	private Version version;
	@SuppressWarnings("rawtypes")
	private VersionMapper versionMapper;
	private CmppDBPlugin dbplugin;

	public VersionHelper2(Version version){
		this.version = version;
	}
	public VersionHelper2(String key,String username,String dataJsonStr){
		version = new Version();
		Date lastmodify = new Date();
		version.setLastmodify(lastmodify);
		version.setData(dataJsonStr);
		version.setKey(key);
		version.setUsername(username);
	}
	public VersionHelper2()
	{
	}
	@SuppressWarnings("unchecked")
	public void save() throws Exception{
		Boolean inserSuccess = false;
		if(version.getId()!=null){
			versionMapper.update(version);
		}else{
			try{
				versionMapper.insert(version);
				inserSuccess=true;
			}catch(Exception ex){
				log.error(ex);
				throw  ex;
			}
			if(inserSuccess){
				try{
					//将表单的值（JSON格式）写到nosql
					long timeStamp = version.getTimestamp();
					String nosqlKey = version.getKey() + "_" + timeStamp;
					dbplugin = (CmppDBPlugin)scriptPluginFactory.getP("cmppDB");
					dbplugin.put(nosqlKey,version.getData());
					log.info("用户user="+version.getUsername()+" 往Nosql插入一条key为 " + nosqlKey + " 的历史记录");
				}catch(Exception ex){
					log.error(ex);	
					throw  ex;
				}
			}
			
		}
	}
	
	public String getFormDataByKey(){
		String dataStr="";
		if(dbplugin==null){
			String nosqlKey = version.getKey() + "_" + version.getTimestamp();
			dbplugin = (CmppDBPlugin)scriptPluginFactory.getP("cmppDB");
			dataStr=dbplugin.getString(nosqlKey);
		}
		return dataStr;
			
	}
	public String getFormDataByKey(String nosqlKey){
		String dataStr="";
		if(dbplugin==null){
			dbplugin = (CmppDBPlugin)scriptPluginFactory.getP("cmppDB");
			dataStr=dbplugin.getString(nosqlKey);
		}
		return dataStr;
			
	}
	/*
	 * getter and setter
	 */
	public Version getVersion() {
		return version;
	}

	public void Version(Version version) {
		this.version = version;
	}

	
	/*public VersionMapper<Version> getVersionMapper() {
		return versionMapper;
	}*/
	public void setVersionMapper(VersionMapper<Version> versionMapper) {
		this.versionMapper = versionMapper;
	}
}
