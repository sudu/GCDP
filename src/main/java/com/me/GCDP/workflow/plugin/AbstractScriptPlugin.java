package com.me.GCDP.workflow.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.w3c.dom.Element;

import com.me.GCDP.util.SpringContextUtil;
import com.me.GCDP.workflow.CmppProcessContext;
import com.me.GCDP.workflow.dm.PersistManagerFactoryHibernateForCmppImpl;
import com.me.GCDP.workflow.model.CmppPluginDef;
import com.me.GCDP.workflow.model.PluginStatus;
import com.ifeng.common.conf.ConfigException;
import com.ifeng.common.conf.ConfigRoot;
import com.ifeng.common.conf.Configurable;
import com.ifeng.common.dm.DataManagerException;
import com.ifeng.common.dm.PersistDataManager;
import com.ifeng.common.dm.persist.hibernate.HibernateConfig;
import com.ifeng.common.plugin.core.abst.AbstLogicPlugin;
import com.me.GCDP.script.ScriptService;
import com.me.GCDP.script.plugin.CoderPlugin;
import com.me.GCDP.script.plugin.ScriptPluginFactory;
import com.me.GCDP.script.plugin.UtilPlugin;

/**
 * 能运行脚本的插件的抽象类，主要封装三种不同类型的脚本插件的一些共有的行为
 * @author xiongfeng
 *
 */
public abstract class AbstractScriptPlugin extends AbstLogicPlugin implements Configurable{
	private static Log log = LogFactory.getLog(AbstractScriptPlugin.class);
	protected static ScriptService scriptService;
	protected static ScriptPluginFactory pluginFactory;
	protected static SessionFactory sessionFactory;
	
	/**
	 * 以下属性会由icommon组件在读取流程定义文件时注入进来
	 */
	protected String nodeId;
	protected String mailTo;
	protected String rtxTo;
	protected String smsTo;
	protected String ids;
	protected String scriptType;
	protected String suspendsOnException;
	protected String exceptionids;
	protected boolean suspendsOnExceptionFlag = false;
	protected String pluginDefId;
	protected String subRoutine = "";
	
	protected String[] idsArr = null;
	protected String[] exceptionidsArr = null;
	
	/**
	 * 配置文件中允许的最大插件执行次数
	 */
	protected int maxExecutionTimes = 0;
	
	protected PersistDataManager dm = new PersistDataManager(PluginStatus.class, 
				new PersistManagerFactoryHibernateForCmppImpl());
	protected PersistDataManager pluginDm = new PersistDataManager(CmppPluginDef.class, 
				new PersistManagerFactoryHibernateForCmppImpl());
//	protected PluginStatus pluginStatus = new PluginStatus();
	
	protected CmppPluginDef pluginDef = null;
	
	/**
	 * 记录插件当前的执行次数
	 */
	protected static int currentExecuteTimes = 0;
	
	public AbstractScriptPlugin() {
	}

	
	static {
		scriptService = (ScriptService) SpringContextUtil.getBean("scriptService");
		pluginFactory = (ScriptPluginFactory) SpringContextUtil.getBean("pluginFactory");
		HibernateConfig.setSessionFactory((SessionFactory)SpringContextUtil.getBean("sessionFactory"));
	}
	
	protected void getMaxExecutionTimesFromConfig() {
		InputStream in = AbstractScriptPlugin.class.getResourceAsStream("/conf/workflow.properties");
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException e) {
			log.error("Exception occured while parsing properties to get plugin.max_executuion_time",e);
		}
		//配置了plugin.max_executuion_time，就按照配置办；没配置，就是5
		maxExecutionTimes = Integer.parseInt(p.getProperty("plugin.max_executuion_time")
				== null ? "5" : p.getProperty("plugin.max_executuion_time"));
	}
	
	protected boolean isExecutionTimeExceeded() {
		if (currentExecuteTimes > maxExecutionTimes) {
			return false;
		}else {
			currentExecuteTimes++;
			return true;
		}
	}
	
	/**
	 * 将ids、exceptionids转换成对应的数组
	 */
	protected void prepareData() {
		if (ids != null && !"".equals(ids.trim())) {
			idsArr = ids.split(","); 
		}
		
		if (exceptionids != null && !"".equals(exceptionids.trim())) {
			exceptionidsArr = exceptionids.split(",");
		}
	}

	/**
	 * 初始化脚本运行状态,初始状态下，脚本运行状态为1(开始状态)
	 */
	protected PluginStatus initPluginStatus(CmppProcessContext processContext) {
		PluginStatus pluginStatus = new PluginStatus();
		pluginStatus.setProcessContext(processContext);
		pluginStatus.setActivityName(processContext.getActivity());
		pluginStatus.setPluginName(pluginDefId);//实际保存插件的Id
		pluginStatus.setPluginStartTime(new Date());
		pluginStatus.setPluginEndTime(null);
		pluginStatus.setStatus((byte)1);
		try {
			pluginDef = (CmppPluginDef) pluginDm.queryById(Integer.parseInt(pluginDefId));
			pluginStatus.setPluginDef(pluginDef);
			dm.add(pluginStatus, null);
		} catch (DataManagerException e2) {
			log.error("Exception occured when initialize PluginStatus of" + pluginDefId, e2);
		} catch (NumberFormatException e) {
			log.error("Parse Plugin Definition Id Error: defIdStr = " + pluginDefId, e);
		}
		return pluginStatus;
	}

	/**
	 * 根据异常配置信息，进行异常报警
	 * @param processContext
	 * @param e
	 */
	protected void doExceptionAlarm(CmppProcessContext processContext, Exception e) {
		if (mailTo == null && rtxTo == null && smsTo == null) {
			return;
		}
		//一旦捕获到异常，根据配置信息，向相关人员发短信、邮件、rtx等
		UtilPlugin util = (UtilPlugin) pluginFactory.getP("util");
		CoderPlugin coder = (CoderPlugin) pluginFactory.getP("coder");
		StringBuffer alarmContent = new StringBuffer();
		String lineSeperator = System.getProperty("line.separator");
		
		alarmContent.append("流程Id： " + processContext.getProcessDefinitionName() + 
				lineSeperator);
		alarmContent.append("流程描述：" + (processContext.getProcessDescription() == null ? ("" + lineSeperator) : (processContext.getProcessDescription() + lineSeperator)));
		alarmContent.append("Activity Id： " + processContext.getActivity() + 
				lineSeperator);
		alarmContent.append("Plugin ID: " + pluginDef.getCfgCode() + lineSeperator);
		alarmContent.append("异常信息： " + e.getMessage() + lineSeperator);
	
		
		if (smsTo != null && !"".equals(smsTo.trim())) {
			String stdMobileStr = smsTo.trim().replace('，', ';').replace(',', ';').replace('；', ';');
			String[] mobiles = stdMobileStr.split(";");
			for(String str : mobiles) {
				//util.sendSMS(str, alarmContent.toString());
			}
			
		}
		
		Map dataPool = (Map) processContext.getData().get("dataPool");
		JSONObject dataPoolJson = JSONObject.fromObject(dataPool);
		//发邮件的话，加入dataPool的情况
		alarmContent.append("dataPool情况：" + dataPoolJson.toString() + lineSeperator);
		if (mailTo != null && !"".equals(mailTo.trim())) {
			String stdMailStr = mailTo.trim().replace('，', ',').replace(';', ',').replace('；', ',');
			
			util.sendMail(stdMailStr, "流程异常", coder.urlEncode(alarmContent.toString(),"UTF-8"), null);
//			String[] mails = stdMailStr.split(";");
//			for(String str : mails) {
//				util.sendMail(str, "流程异常", alarmContent.toString(), null);
//			}
		}
		/*if (rtxTo != null && !"".equals(rtxTo.trim())) {
			util.sendRTX(rtxTo, alarmContent.toString());
		}*/
	}

	/**
	 * 出现异常时，修改插件的运行状态信息，并保存进数据库
	 */
	protected void modifyExceptionPluginStatus(PluginStatus pluginStatus) {
		pluginStatus.setStatus((byte)2);
		try {
			dm.modify(pluginStatus, new String[]{"status"}, null);
		} catch (DataManagerException e1) {
			log.error("Exception occured when modify PluginStatus" + pluginDefId, e1);
		}
	}

	/**
	 * 修改正常结束的流程状态信息，并保存进数据库
	 */
	protected void modifyNormalTerminatedPluginStatus(PluginStatus pluginStatus) {
		//没有出现异常，修改流程状态
		pluginStatus.setPluginEndTime(new Date());
		pluginStatus.setStatus((byte)3);
		try {
			dm.modify(pluginStatus, new String[]{"status","pluginEndTime"}, null);	
		} catch (DataManagerException e) {
			log.error("Exception occured when save ending PluginStatus of " + pluginDefId, e);
		}
	}
	
	/**
	 * 修改挂起状态信息，并存进数据库
	 */
	protected void modifyPendingPluginStatus(PluginStatus pluginStatus) {
		pluginStatus.setStatus((byte)4);
		try {
			dm.modify(pluginStatus, new String[]{"status"}, null);
		} catch (DataManagerException e1) {
			log.error("Exception occured when modify PluginStatus" + pluginDefId, e1);
		}
	}
	
	
	
	@Override
	public Object config(ConfigRoot configRoot, Object parent, Element configEle)
			throws ConfigException {
		subRoutine = configEle.getAttribute("sub-routine");
		return this;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	public void setRtxTo(String rtxTo) {
		this.rtxTo = rtxTo;
	}

	public void setSmsTo(String smsTo) {
		this.smsTo = smsTo;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public void setSuspendsOnException(String suspendsOnException) {
		this.suspendsOnException = suspendsOnException;
	}

	public void setExceptionids(String exceptionids) {
		this.exceptionids = exceptionids;
	}

	public void setPluginDefId(String defIdStr) {
		this.pluginDefId = defIdStr;
	}

	public void setSubRoutine(String subRoutine) {
		this.subRoutine = subRoutine;
	}

	public String getPluginDefId() {
		return pluginDefId;
	}

	
}