package com.me.GCDP.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-8-9              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class SourceSubscribeInfo extends BaseModel {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private AtomicLong pushErrCount = new AtomicLong(0);
	private AtomicLong scriptErrCount = new AtomicLong(0);
	private AtomicLong totalCount = new AtomicLong(0);
	private AtomicLong lastPushErrCount = new AtomicLong(0);
	private AtomicLong lastScriptErrCount = new AtomicLong(0);
	
	//最后推送日期，1970.1.1以来的秒数，长度10
	private Long lastPushDate = null;
	
	private Integer lastId = null;
	
	private String lastErrInfo = null;
	
	public String getLastErrInfo() {
		return lastErrInfo;
	}

	public void setLastErrInfo(String lastErrInfo) {
		this.lastErrInfo = lastErrInfo;
	}

	public Integer getLastId() {
		return lastId;
	}

	public void setLastId(Integer lastId) {
		this.lastId = lastId;
	}

	public Long getLastPushDate() {
		return lastPushDate;
	}
	
	public String getLastPushDateStr(){
		if(lastPushDate != null){
			Date d = new Date();
			d.setTime(lastPushDate*1000);
			return sdf.format(d);
		}else{
			return null;
		}
	}

	public void setLastPushDate(Long lastPushDate) {
		this.lastPushDate = lastPushDate;
	}

	public AtomicLong getPushErrCount() {
		return pushErrCount;
	}

	public void setPushErrCount(AtomicLong pushErrCount) {
		this.pushErrCount = pushErrCount;
	}

	public AtomicLong getScriptErrCount() {
		return scriptErrCount;
	}

	public void setScriptErrCount(AtomicLong scriptErrCount) {
		this.scriptErrCount = scriptErrCount;
	}

	public AtomicLong getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(AtomicLong totalCount) {
		this.totalCount = totalCount;
	}

	public AtomicLong getLastPushErrCount() {
		return lastPushErrCount;
	}

	public void setLastPushErrCount(AtomicLong lastPushErrCount) {
		this.lastPushErrCount = lastPushErrCount;
	}

	public AtomicLong getLastScriptErrCount() {
		return lastScriptErrCount;
	}

	public void setLastScriptErrCount(AtomicLong lastScriptErrCount) {
		this.lastScriptErrCount = lastScriptErrCount;
	}
	
}
