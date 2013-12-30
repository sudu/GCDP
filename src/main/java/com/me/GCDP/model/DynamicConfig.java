package com.me.GCDP.model;

public class DynamicConfig extends BaseModel{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String svrIp;
	private String svrPath;
	private int status;
	private String ext;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String cache;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSvrIp() {
		return svrIp;
	}
	public void setSvrIp(String svrIp) {
		this.svrIp = svrIp;
	}
	public String getSvrPath() {
		return svrPath;
	}
	public void setSvrPath(String svrPath) {
		this.svrPath = svrPath;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPwd() {
		return dbPwd;
	}
	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}
	public String getCache() {
		return cache;
	}
	public void setCache(String cache) {
		this.cache = cache;
	}
	
	

}
