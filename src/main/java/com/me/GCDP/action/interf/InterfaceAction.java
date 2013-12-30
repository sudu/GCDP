package com.me.GCDP.action.interf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.struts2.ServletActionContext;

import com.me.GCDP.freemarker.FreeMarkerHelper;
import com.me.GCDP.mapper.InterfaceMapper;
import com.me.GCDP.model.Interface;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptType;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>Title: 用户定义接口Action</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-21              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class InterfaceAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(InterfaceAction.class);
	
	//节点ID
	private Integer nodeid = null;
	
	private String result = null;
	
	private Integer interface_id = null;

    private boolean debug = true;
	
	private ScriptService scriptService = null;
	private InterfaceMapper<Interface> ifMapper = null;

	@Override
	@SuppressWarnings("unchecked")
	public String execute() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		Map<String, Object> pMap = new HashMap<String, Object>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String post_data = null;
			String str = null;
			while((str = br.readLine()) != null){
				if(post_data == null){
					post_data = str;
				}else{
					post_data += ("\r\n" + str);
				}
			}
			if(post_data != null && !post_data.equals("")){
				pMap.put("POSTDATA", post_data);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Interface interf = new Interface();
		interf.setId(interface_id);
		List<Interface> iflist = ifMapper.get(interf);
		interf = (iflist == null || iflist.size() ==0 ? null : iflist.get(0));
		if(interf != null){
			nodeid = interf.getNodeid();
			Map<String, String[]> paramMap = request.getParameterMap();
			try {
				Iterator<String> iter = paramMap.keySet().iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String[] values = (String[])paramMap.get(key);
					pMap.put(key, values[0]);
				}
				//scriptService.runInterfaceScript(interface_id.toString(), pMap);
                if(isDebug()){
                    scriptService.runDebug(nodeid,pMap,ScriptType.interf,interface_id.toString());
                }else{
                    scriptService.run(nodeid, pMap, ScriptType.interf, interface_id.toString());
                }
			} catch (Exception e) {
				log.error(e.getMessage());
				result = "脚本运行时异常：" + e.getMessage();
				response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				return "result";
			}
			
			//String template = scriptService.openInterfaceTemplate(interface_id.toString());
			String template = null;
			try {
                if(isDebug()){
                    template = scriptService.openDebug(nodeid, ScriptType.interf_template, interface_id.toString());
                }else{
                    template = scriptService.open(nodeid,ScriptType.interf_template,interface_id.toString());
                }
			} catch (IOException e) {
				log.error(e.getMessage());
				result = "读取模板失败：" + e.getMessage();
				response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				return "result";
				
			}
			if(template != null && !template.equals("")){
				try {
					result = FreeMarkerHelper.process2(template, pMap);
				} catch (Exception e) {
					log.error(e);
					result = "模板渲染错误：" + e.getMessage();
				}
			}
		}else{
			result = "未知接口";
			response.setStatus(HttpStatus.SC_NOT_FOUND);
		}
		return "index";
	}
	
	public String doc(){
		return "doc";
	}

	/*
	 * getter and setter
	 */
	public String getResult() {
		return result;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public Integer getInterface_id() {
		return interface_id;
	}

	public void setInterface_id(Integer interface_id) {
		this.interface_id = interface_id;
	}

	public void setIfMapper(InterfaceMapper<Interface> ifMapper) {
		this.ifMapper = ifMapper;
	}

	public Integer getNodeid() {
		return nodeid;
	}

	public void setNodeid(Integer nodeid) {
		this.nodeid = nodeid;
	}


    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}