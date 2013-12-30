package com.me.GCDP.source;

import java.util.Map;

import com.me.GCDP.mapper.SourceMapper;
import com.me.GCDP.model.Source;
import com.me.GCDP.util.oscache.OSCache;
import com.me.GCDP.script.ScriptService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-25              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SubscribeService {
	
	//private static Log log = LogFactory.getLog(SubscribeService.class);
	
	private SourceMapper<Source> sourceMapper = null;
	
	private ScriptService scriptService = null;
	
	private OSCache oscache_sta = null;
	/**
	 * 对源数据进行过滤处理 
	 * @param formid
	 * @param dataPool
	 */
	public void process(Integer nodeid, Integer formid, Map<String, Object> dataPool){
		SubscribeHandler handler = new SubscribeHandler();
		handler.setNodeId(nodeid);
		handler.setFormId(formid);
		handler.setDataPool(dataPool);
		handler.setSourceMapper(sourceMapper);
		handler.setScriptService(scriptService);
		handler.setOscache_sta(oscache_sta);
		handler.start();
	}

	public void setSourceMapper(SourceMapper<Source> sourceMapper) {
		this.sourceMapper = sourceMapper;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setOscache_sta(OSCache oscache_sta) {
		this.oscache_sta = oscache_sta;
	}
	
}
