package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-18              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SourceSubscribe extends BaseModel {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * 定阅状态枚举
	 * @author huwq
	 *
	 */
	public enum SubscribeStatus{
		NORMAL(0),//正常
		CHECKING(1),//等待审核
		STOP(2),//停止推送
		SCRIPT_ERROR(3),//脚本错误
		PUSH_ERROR(4),//推送错误
		ERROR_STOP(5);//有错误自动中止
		
		Integer value = null;
		SubscribeStatus(Integer value){
			this.value = value;
		}
		
		public static SubscribeStatus getInstance(Integer value){
			switch(value){
				case 0:
					return NORMAL;
				case 1:
					return CHECKING;
				case 2:
					return STOP;
				case 3:
					return SCRIPT_ERROR;
				case 4:
					return PUSH_ERROR;
				case 5:
					return ERROR_STOP;
				default :
					return null;
			}
		}
		
		Integer getValue(){
			return this.value;
		}
		
	}
	
	//定阅信息类
	private SourceSubscribeInfo info = null;

	//源id
	private Integer sourceId = null;
	
	//回调接口url
	private String callBackUrl = null;
	
	//回调方法，post/get
	private String method = null;
	
	//定阅日期，1970.1.1以来的秒数，长度10
	private Long subscribeDate = null;
	
	//当前状态
	private SubscribeStatus status = null;
	
	//创建人
	private String creator = null;
	
	private String script = null;
	
	//动态前端id,多个用逗号分隔
	private String dynIds = null;
	
	private String name = null;
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public Long getSubscribeDate() {
		return subscribeDate;
	}
	
	public String getSubscribeDateStr(){
		if(subscribeDate != null){
			Date d = new Date();
			d.setTime(subscribeDate*1000);
			return sdf.format(d);
		}else{
			return null;
		}
	}

	public void setSubscribeDate(Long subscribeDate) {
		this.subscribeDate = subscribeDate;
	}

	public SubscribeStatus getStatus() {
		return status;
	}
	
	public Integer getStatusCode(){
		return status.getValue();
	}

	public void setStatus(SubscribeStatus status) {
		this.status = status;
	}
	
	public void setStatusCode(Integer status){
		this.status = SubscribeStatus.getInstance(status);
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public SourceSubscribeInfo getInfo() {
		return info;
	}

	public void setInfo(SourceSubscribeInfo info) {
		this.info = info;
	}

	public String getDynIds() {
		return dynIds;
	}

	public void setDynIds(String dynIds) {
		this.dynIds = dynIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
