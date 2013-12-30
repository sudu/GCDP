package com.me.GCDP.action.script;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.CookiesAware;

import com.me.GCDP.freemarker.FreeMarkerPlugin;
import com.me.GCDP.mapper.ScriptMapper;
import com.me.GCDP.model.Script;
import com.me.GCDP.security.SecurityPlugin;
import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.util.property.CmppConfig;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.ScriptThreadLocal;
import com.me.GCDP.script.ScriptType;
import com.me.GCDP.script.plugin.HttpPlugin;
import com.me.GCDP.script.plugin.LogPlugin;
import com.me.GCDP.script.plugin.PluginDoc;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.json.JSONArray;
import com.me.json.JSONObject;
import com.opensymphony.xwork2.ActionSupport;

import freemarker.template.TemplateException;

/**
 * <p>Title: 脚本Action</p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huweiqi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-1-28              huweiqi               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class ScriptAction extends ActionSupport implements CookiesAware {
	
	private static Log log = LogFactory.getLog(ScriptAction.class);
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> cookieMap = null;
	private ScriptPluginFactory pluginFactory = null;
	
	private String msg = null;
	private Boolean hasError = false;

	private String script = null;
	private String result = "";
	private Integer timeout = 0; 
	private String pname = null;
	
	private PluginDoc pdoc = null;
	private Map<String, PluginDoc> docMap = null;
    private JSONArray cat = null;
	private ScriptService scriptService = null;

	//节点id
	private Integer nodeId = null;
	//ID2
	private String id2 = null;
	//ID1
	private String id1 = null;
	private String version = null;
	private String stype = null;

    // 选用默认模板名称
    private String tpl = null;
    private JSONArray vlist;
    private long length = 100000L;
	private String search = null;
	private String beginTime = null;
	private String endTime = null;

	//公共脚本
	private Script ss = new Script();
	private List<Script> slist = null;
	private ScriptMapper<Script> scriptMapper = null;
	
	private String ids = null;
	
	//调试注入变量
	private String variables = null;
	
	public String execute() throws Exception {
		return "main";
	}
	
	/**
	 * 获取指定脚本的日志
	 * @return
	 */
	public String getLog(){
		
		if(stype == null){
			stype = "form";
		}
		if(search!=null&& !search.equals("") &&!search.equals("null")){
			try {
				search = new String(search.getBytes("iso-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}
		}
		String daemonize = CmppConfig.getKey("cmpp.daemonize");
		//计划任务特别处理
		if("task".equalsIgnoreCase(stype)) {
			if("no".equalsIgnoreCase(daemonize) || 
					"n".equalsIgnoreCase(daemonize)) {
				HttpPlugin http = new HttpPlugin();
				HttpServletRequest request = ServletActionContext.getRequest();
				String bgUrl = CmppConfig.getKey("cmpp.daemonize.baseurl") + 
						"/runtime/getScriptLog.jhtml?nodeId=" + 
						nodeId + "&id1=" + id1 + "&id2=" + id2 + "&stype=" + stype;
				Map<String, String> reqProps = new HashMap<String, String>();
				reqProps.put("Cookie", request.getHeader("Cookie"));
				try {
					result = http.sendGet(bgUrl, reqProps);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				return "PureLogText";
			}else {
				if(id1 != null && nodeId !=null && stype != null){
					LogPlugin logplugin = new LogPlugin();
					if(id2==null || id2.equals("") || id2.equals("null")){
						try {
							result = logplugin.getLogText(beginTime,endTime,search,length, nodeId.toString(),stype,id1);
						} catch (ParseException e) {
							log.error(e);
						}
					}else {
							
						try {
							result = logplugin.getLogText(beginTime,endTime,search,length, nodeId.toString(),stype,id1,id2);
						} catch (ParseException e) {
							log.error(e);
						}
					}
				}else{
					result = "error";
				}
				return "LogText";
			}
			
		}else {
			if(id1 != null && nodeId !=null && stype != null){
				LogPlugin logplugin = new LogPlugin();
				if(id2==null || id2.equals("") || id2.equals("null")){
					try {
						result = logplugin.getLogText(beginTime,endTime,search,length, nodeId.toString(),stype,id1);
					} catch (ParseException e) {
						log.error(e);
					}
				}else {
						
					try {
						result = logplugin.getLogText(beginTime,endTime,search,length, nodeId.toString(),stype,id1,id2);
					} catch (ParseException e) {
						log.error(e);
					}
				}
			}else{
				result = "error";
			}
			return "LogText";
		}
	}
	
	/**
	 * 获取指定版本的脚本内容
	 * @return
	 */
	public String openByVer(){
		if(id1 != null && nodeId !=null && version != null){
			try{
				if(id2==null || id2.equals("") || id2.equals("null")){
					script = scriptService.open(nodeId, version, ScriptType.getInstance(stype), id1);
				}else{
					script = scriptService.open(nodeId, version, ScriptType.getInstance(stype), id1, id2);
				}
			}catch(Exception e){
				log.error(e.getMessage());
				result = "error";
			}
		}else{
			result = "error";
		}
		return "OpenAndSave";
	}
	
	/**
	 * 获取脚本版本列表
	 * @return
	 */
	public String getVersionList(){
		if(id1 != null && nodeId !=null){
			try {
				if(id2==null || id2.equals("") || id2.equals("null")){
					vlist = scriptService.getVersionStateList(nodeId, ScriptType.getInstance(stype), id1);
				}else{
					vlist = scriptService.getVersionStateList(nodeId, ScriptType.getInstance(stype), id1, id2);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				result = "error";
			}
		}else{
			result = "error";
		}
        vlist = vlist == null ? new JSONArray(): vlist;
		return "versionList";
	}
	
	/**
	 * 获取脚本
	 * @return
	 */
	public String open(){
		if(id1 != null && nodeId !=null){
			try{
				if(id2==null || id2.equals("") || id2.equals("null")){
					script = scriptService.openLatest(nodeId, ScriptType.getInstance(stype), id1);
				}else{
					script = scriptService.openLatest(nodeId, ScriptType.getInstance(stype), id1, id2);
				}
			}catch(Exception e){
				log.error(e.getMessage());
				result = "error";
			}
		}else{
			result = "error";
		}
        // 没有取到脚本内容，返回默认
        if(script==null){
            try {
                script = getDefaultTemplate(nodeId, ScriptType.getInstance(stype), id1, id2, tpl);
            } catch (IOException e) {
                log.error(e);
            } catch (TemplateException e) {
                log.error(e);
            }
        }
		return "OpenAndSave";
	}

    private String getDefaultTemplate(Integer nodeId, ScriptType stype, String id1, String id2, String tplName) throws IOException, TemplateException {
        ScriptPluginFactory pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
        SecurityPlugin userPlugin  = (SecurityPlugin)pluginFactory.getP("user");
        if(userPlugin.isLogin()==false) {
            return null;
        }
        if(tplName == null || tplName.isEmpty()) {
            tplName = stype.toString();
        }
        String relativePath = "develop/script/default/" + tplName + ".ftl";
        String path = ServletActionContext.getServletContext().getRealPath(relativePath);
        if (path == null) {
            return null;
        }
        String tpl = FileUtils.readFileToString(new File(path), Charset.forName("UTF-8"));
        FreeMarkerPlugin renderer = (FreeMarkerPlugin) pluginFactory.getP("freemarker");
        String userName = userPlugin.getUserName();
        String userId = userPlugin.getUserId();

        Map<String, Object> data = new HashMap();
        data.put("userId", userId);
        data.put("userName", userName);

        Map<String, String> ssoInfo = userPlugin.getUserInfoFromSSO(userId);
        data.putAll(ssoInfo);

        data.put("nodeId", nodeId);
        data.put("id1", id1);
        data.put("id2", id2);

        HttpServletRequest request = ServletActionContext.getRequest();
        String schema = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        StringBuffer url = new StringBuffer();
        url.append(schema).append("://").append(host);
        if (port != 80) {
            url.append(':').append(port);
        }
        if (ScriptType.dynPage.equals(stype)) {
            url.append("/Cmpp/develop/dynamic/view.jhtml?").append("nodeId=").append(nodeId).append('&').append("id=").append(id1);
        } else if (ScriptType.interf.equals(stype)) {
            url.append("/Cmpp/develop/interface_mgr!view.jhtml?").append("nodeId=").append(nodeId).append('&').append("interf.id=").append(id1);
        } else if (ScriptType.task.equals(stype)) {
            url.append("/Cmpp/develop/task/taskMgr.html?").append(nodeId).append('&').append("taskId=").append(id1);
        } else if (ScriptType.subscribe.equals(stype)) {
            url.append("/Cmpp/develop/source/index.jhtml?").append(nodeId).append('&').append("id=").append(id1);
        } else {
            url.append("/Cmpp/develop/scriptdebug.jhtml").append("?nodeId=").append(nodeId).append("&id1=").append(id1).append("&id2=").append(id2).append("&stype=").append(stype);
            if (ScriptType.process.equals(stype)) {
                url.append('[').append(schema).append("://").append(host);
                if (port != 80) {
                    url.append(':').append(port);
                }
                url.append("/Cmpp/develop/workflowMgr.jhtml?").append(nodeId).append('&').append("processId=").append(id1).append(']');
            }
        }
        data.put("cmppUrl", url.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.put("today", sdf.format(new Date()));
        return renderer.process(tpl, data);
    }

	/**
	 * 保存代码 以debug 方式保存
	 * @return
	 */
	public String save(){
		if(id1 != null && nodeId !=null){
			try{
				if(id2==null || id2.equals("") || id2.equals("null")){
					scriptService.saveDebug(nodeId, script, ScriptType.getInstance(stype), id1);
				}else{
					scriptService.saveDebug(nodeId, script, ScriptType.getInstance(stype), id1, id2);
				}
				result = "success";
				script = null;
			}catch(Exception e){
				log.error(e.getMessage());
				result = "error:"+e.getMessage();
			}
		}else{
			result = "error";
		}
		return "result";
	}


    /**
     * 执行发布，以 release 状态保存脚本
     * @return
     */
    public String release(){
        if(id1 != null && nodeId !=null){
            try{
                if(id2 == null || id2.equals("") || id2.equals("null")){
                    scriptService.save(nodeId,script,ScriptType.getInstance(stype),id1);
                }else{
                    scriptService.save(nodeId,script,ScriptType.getInstance(stype),id1,id2);
                }
                result = "success";
                script = null;
            }catch(Exception e){
                log.error(e.getMessage());
                result = "error:"+e.getMessage();
            }
        }else{
            result = "error";
        }
        return "result";
    }

	/**
	 * 执行脚本
	 * @return
	 */
	public String eval(){
		ScriptThreadLocal.remove();
		ScriptThreadLocal.setOutput(true);
		try {
			//script = URLDecoder.decode(script, "UTF-8");
			Map<String,Object> dataPool = new HashMap<String,Object>();
			if(variables != null && !variables.equals("")){
				JSONArray array = new JSONArray(variables);
				for(int i = 0 ; i < array.length() ; i++ ){
					JSONObject jobj = (JSONObject)array.get(i);
					dataPool.put(jobj.getString("vKey"), jobj.getString("vValue"));
				}
			}
			scriptService.run(nodeId, script, dataPool, timeout == null ? 0 : timeout);
			result = ScriptThreadLocal.getOutputString();
			if(result == null){
				result = "Success";
			}else{
				result = result.replaceAll("\r\n", "<br />");
				result += "Success";
			}
		} catch (Exception e) {
			log.error("ScriptAction::eval():", e);
			result = ScriptThreadLocal.getOutputString();
			if(result == null){
				result = "脚本运行时异常：" + e.getMessage();
			}else{
				result = result.replaceAll("\r\n", "<br />");
				result += "脚本运行时异常：" + e.getMessage();
			}
		}finally {
			ScriptThreadLocal.removeScriptIds();
			ScriptThreadLocal.remove();
		}
		return "eval";
	}
	
	/**
	 * 获取插件文档
	 * @return
	 */
	public String pluginDoc(){
		Map<String, PluginDoc> docMap = pluginFactory.getDocMap();
		pdoc = docMap.get(pname);
		return "pluginDoc";
	}
	
	/**
	 * 获取插件文档列表
	 * @return
	 */
	public String pluginList() {
        //docMap = pluginFactory.getDocMap();
        cat = pluginFactory.getCat();
        return "plist";
    }

	/**
	 * 获取公共脚本列表
	 * @return
	 */
	public String getCommonScriptList(){
		if(nodeId != null){
			ss.setNodeid(nodeId);
			
			if(ss.getName() != null && ss.getName().indexOf(",")>=0){
				String[] sss = ss.getName().split("\\,");
				slist = new ArrayList<Script>();
				for(String s : sss){
					ss.setName(s);
					List<Script> sl = scriptMapper.get(ss);
					slist.addAll(sl);
				}
			}else{
				slist = scriptMapper.get(ss);
			}
		}
		return "commonScriptList";
	}
	
	/**
	 * 获取公共脚本详细资料
	 * @return
	 */
	public String getCommonScript(){
		if(ss.getId() != null && nodeId != null){
			List<Script> slist = scriptMapper.get(ss);
			if(slist != null && slist.size() > 0){
				ss = slist.get(0);
				try {
					String s_txt = scriptService.open(nodeId, ScriptType.common, ss.getName());
					ss.setScript(s_txt);
				} catch (IOException e) {
					log.error(e.getMessage());
					ss.setScript(e.getMessage());
				}
			}
		}else{
			ss = null;
		}
		return "commonScript";
	}
	
	/**
	 * 创建公共脚本
	 * @return
	 */
	public String addCommonScript(){
		if(ss.getName() == null || ss.getName().equals("")){
			msg = "ss.name is null";
			hasError = true;
			return "msg";
		}
		
		if(nodeId == null){
			msg = "nodeId is null";
			hasError = true;
			return "msg";
		}
		
		Script s2 = new Script();
		s2.setNodeid(nodeId);
		s2.setName2(ss.getName());
		List<Script> tmpList = scriptMapper.get(s2);
		if(tmpList != null && tmpList.size() > 0){
			msg = "ss.name existed";
			hasError = true;
			return "msg";
		}
		
		try{
			String user = cookieMap.get("cmpp_user");
			ss.setCreator(user);
			ss.setCreateDate(System.currentTimeMillis()/1000);
			ss.setNodeid(nodeId);
			scriptMapper.insert(ss);
			
			if(ss.getScript() != null && !ss.getScript().equals("")){
				scriptService.save(nodeId, ss.getScript(), ScriptType.common, ss.getName());
			}
			hasError = false;
		}catch(Exception e){
			log.error("addCommonScript: " + e.getMessage());
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 更新公共脚本信息及脚本内容
	 * @return
	 */
	public String updateCommonScript(){
		if(ss.getId() == null){
			msg = "ss.id is null";
			hasError = true;
			return "msg";
		}
		
		if(nodeId == null){
			msg = "nodeId is null";
			hasError = true;
			return "msg";
		}
		
		try{
			if(ss.getIntro() != null && !ss.getIntro().equals("")){
				scriptMapper.update(ss);
			}
			
			if(ss.getScript() != null && !ss.getScript().equals("")){
				String script = ss.getScript();
				ss = scriptMapper.get(ss).get(0);
				if(ss != null){
					scriptService.save(nodeId, script, ScriptType.common, ss.getName());
				}
			}
			hasError = false;
		}catch(Exception e){
			log.error("updateCommonScript: " + e.getMessage(), e);
			msg = e.getMessage();
			hasError = true;
		}
		return "msg";
	}
	
	/**
	 * 删除公共脚本
	 * @return
	 */
	public String deleteCommonScript(){
		if(nodeId == null){
			msg = "nodeId is null";
			hasError = true;
			return "msg";
		}
		
		if(ss.getId() == null){
			if(ids == null || ids.equals("")){
				msg = "ss.id is null";
				hasError = true;
				return "msg";
			}else{
				String[] idss = ids.split("\\,");
				try{
					for(String id : idss){
						ss.setId(Integer.parseInt(id));
                        List<Script> list = scriptMapper.get(ss);
                        if (list != null && list.size() > 0) {
                            for (Script s : list) {
                                scriptService.delete(nodeId, ScriptType.common, s.getName());
                            }
                        }
                        scriptMapper.delete(ss);
                    }
				}catch(Exception e){
					log.error("deleteCommonScript: " + e.getMessage(), e);
					msg = e.getMessage();
					hasError = true;
				}
			}
		}else{
			try{
				scriptMapper.delete(ss);
			}catch(Exception e){
				log.error("deleteCommonScript: " + e.getMessage());
				msg = e.getMessage();
				hasError = true;
			}
		}
		return "msg";
	}
	
	/* setter and getter */

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getResult() {
		return result;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public void setPluginFactory(ScriptPluginFactory pluginFactory) {
		this.pluginFactory = pluginFactory;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public PluginDoc getPdoc() {
		return pdoc;
	}

	public Map<String, PluginDoc> getDocMap() {
		return docMap;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public JSONArray getVlist() {
        return this.vlist;
    }

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setStype(String stype) {
		this.stype = stype;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public String getId1() {
		return id1;
	}

	public void setId1(String id1) {
		this.id1 = id1;
	}

	public String getStype() {
		return stype;
	}

	public void setLength(long length) {
		this.length = length;
	}
	
	public Script getSs() {
		return ss;
	}

	public void setSs(Script ss) {
		this.ss = ss;
	}

	public List<Script> getSlist() {
		return slist;
	}

	public void setScriptMapper(ScriptMapper<Script> scriptMapper) {
		this.scriptMapper = scriptMapper;
	}

	@Override
	public void setCookiesMap(Map<String, String> arg0) {
		this.cookieMap = arg0;
	}

	public String getMsg() {
		return msg;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setVariables(String variables) {
		this.variables = variables;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

    public JSONArray getCat(){
        return cat;
    }

    public String getTpl() {
        return tpl;
    }

    public void setTpl(String tpl) {
        this.tpl = tpl;
    }

//	public static void main(String[] args) throws UnsupportedEncodingException{
//		System.out.println(new String(null.getBytes("iso-8859-1"),"UTF-8"));
//	}
	
}
