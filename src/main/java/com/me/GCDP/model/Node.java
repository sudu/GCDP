package com.me.GCDP.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.me.GCDP.util.Hanyu2Pinyin;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-6-30              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class Node extends BaseModel {
	
	public void print(){
		System.out.println(this.getEnvMapJson());
		System.out.println(this.getNosqlHostJson());
		System.out.println(this.getMasterDBJson());
		System.out.println(this.getSlaveDBJson());
		System.out.println(this.getPrivateSearchHostsJson());
		System.out.println(this.getPublicSearchHostsJson());
		System.out.println(this.getRemoteHostJson());
	}
	
	private static final long serialVersionUID = 1L;
	
	private String name = null;
	
	private RemoteHost remoteHost = null;
	
	private DataBaseHost masterDB = null;
	
	private DataBaseHost slaveDB = null;
	
	private List<SearchHost> publicSearchHosts = null;
	
	private List<SearchHost> privateSearchHosts = null;
	
	private NosqlHost nosqlHost = null; 
	
	private List<Map<String, Object>> envMap = null;
	
	public String getQuanPin(){
		JSONArray array = new JSONArray(Hanyu2Pinyin.getPinyin(name));
		return array.toString();
	}
	
	public String getJianPin(){
		JSONArray array = new JSONArray(Hanyu2Pinyin.getJianpin(name));
		return array.toString();
	}
	
	public String getRuntimeUrl(){
		return "http://"+this.remoteHost.getIp()+"/CmppRT/";
	}
	
	public String getRemoteHostJson(){
		return getJsonByObj(remoteHost);
	}
	
	public void setRemoteHostJson(String jsonStr){
		this.remoteHost = new RemoteHost(jsonStr); 
	}
	
	public String getMasterDBJson(){
		return getJsonByObj(masterDB);
	}
	
	public void setMasterDBJson(String jsonStr){
		this.masterDB = new DataBaseHost(jsonStr);
	}
	
	public String getSlaveDBJson(){
		return getJsonByObj(slaveDB);
	}
	
	public void setSlaveDBJson(String jsonStr){
		this.slaveDB = new DataBaseHost(jsonStr);
	}
	
	public String getPublicSearchHostsJson(){
		return getJsonByList(publicSearchHosts);
	}
	
	public void setPublicSearchHostsJson(String jsonStr) throws JSONException{
		publicSearchHosts = new ArrayList<SearchHost>();
		JSONArray jsonArray = new JSONArray(jsonStr);
		for(int i = 0 ; i < jsonArray.length() ; i ++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			SearchHost shost = new SearchHost(jsonObj.toString());
			publicSearchHosts.add(shost);
		}
	}
	
	public String getPrivateSearchHostsJson(){
		return getJsonByList(privateSearchHosts);
	}
	
	public void setPrivateSearchHostsJson(String jsonStr) throws JSONException{
		privateSearchHosts = new ArrayList<SearchHost>();
		JSONArray jsonArray = new JSONArray(jsonStr);
		for(int i = 0 ; i < jsonArray.length() ; i ++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			SearchHost shost = new SearchHost(jsonObj.toString());
			privateSearchHosts.add(shost);
		}
	}
	
	public String getNosqlHostJson(){
		return getJsonByObj(nosqlHost);
	}
	
	public void setNosqlHostJson(String jsonStr){
		this.nosqlHost = new NosqlHost(jsonStr);
	}
	
	public String getEnvMapJson(){
		return getJsonByList(envMap);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setEnvMapJson(String jsonStr) throws JSONException{
		envMap = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = new JSONArray(jsonStr);
		for(int i = 0 ; i < jsonArray.length() ; i ++){
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			Map map = jsonObj.toMap();
			envMap.add(map);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private String getJsonByList(List list){
		String json = null;
		if(list != null){
			JSONArray jsonArray = new JSONArray(list);
			json = jsonArray.toString();
		}
		return json;
	}
	
	private String getJsonByObj(Host host){
		String json = null;
		if(host != null){
			JSONObject jsonobj = new JSONObject(host);
			json = jsonobj.toString();
		}
		return json;
	}
	
	/*
	 * getter and setter 
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public RemoteHost getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(RemoteHost remoteHost) {
		this.remoteHost = remoteHost;
	}

	public DataBaseHost getMasterDB() {
		return masterDB;
	}

	public void setMasterDB(DataBaseHost masterDB) {
		this.masterDB = masterDB;
	}

	public DataBaseHost getSlaveDB() {
		return slaveDB;
	}

	public void setSlaveDB(DataBaseHost slaveDB) {
		this.slaveDB = slaveDB;
	}
	
	public NosqlHost getNosqlHost() {
		return nosqlHost;
	}

	public void setNosqlHost(NosqlHost nosqlHost) {
		this.nosqlHost = nosqlHost;
	}
	
	public List<SearchHost> getPublicSearchHosts() {
		return publicSearchHosts;
	}

	public void setPublicSearchHosts(List<SearchHost> publicSearchHosts) {
		this.publicSearchHosts = publicSearchHosts;
	}

	public List<SearchHost> getPrivateSearchHosts() {
		return privateSearchHosts;
	}

	public void setPrivateSearchHosts(List<SearchHost> privateSearchHosts) {
		this.privateSearchHosts = privateSearchHosts;
	}

	public List<Map<String, Object>> getEnvMap() {
		return envMap;
	}

	public void setEnvMap(List<Map<String, Object>> envMap) {
		this.envMap = envMap;
	}

	/*
	 * 主机类
	 */
	public class Host{
		private String ip = null;
		
		private Integer port = null;
		
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

	}
	
	/*
	 * 搜索主机类
	 */
	public class SearchHost extends Host{
		
		public SearchHost(){}
		
		public SearchHost(String jsonStr){
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				setIp(jsonObj.optString("ip"));
				setPort(jsonObj.optInt("port"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 数据库主机类
	 */
	public class DataBaseHost extends Host{
		
		private String dbname = null;
		
		private String uname = null;
		
		private String pwd = null;
		
		private boolean isWritable = true;
		
		private boolean isReadable = true;
		
		public DataBaseHost(){}
		
		public DataBaseHost(String jsonStr){
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				setIp(jsonObj.optString("ip"));
				setPort(jsonObj.optInt("port"));
				setUname(jsonObj.optString("uname"));
				setPwd(jsonObj.optString("pwd"));
				setDbname(jsonObj.optString("dbname"));
				setWritable(jsonObj.optBoolean("writable"));
				setReadable(jsonObj.optBoolean("readable"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getUname() {
			return uname;
		}

		public void setUname(String uname) {
			this.uname = uname;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

		public String getDbname() {
			return dbname;
		}

		public void setDbname(String dbname) {
			this.dbname = dbname;
		}

		public boolean isWritable() {
			return isWritable;
		}

		public void setWritable(boolean isWritable) {
			this.isWritable = isWritable;
		}

		public boolean isReadable() {
			return isReadable;
		}

		public void setReadable(boolean isReadable) {
			this.isReadable = isReadable;
		}
		
	}

	/*
	 * 远程连接主机类
	 */
	public class RemoteHost extends Host{
		
		private String uname = null;
		
		private String pwd = null;
		
		//远程程序部署路径
		private String remoteDeployPath = null;
		
		//远程数据存储路径
		private String remoteDataPath = null;
		
		public RemoteHost(){}
		
		public RemoteHost(String jsonStr){
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				setIp(jsonObj.optString("ip"));
				setPort(jsonObj.optInt("port"));
				setUname(jsonObj.optString("uname"));
				setPwd(jsonObj.optString("pwd"));
				setRemoteDeployPath(jsonObj.optString("remoteDeployPath"));
				setRemoteDataPath(jsonObj.optString("remoteDataPath"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public String getUname() {
			return uname;
		}

		public void setUname(String uname) {
			this.uname = uname;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

		public String getRemoteDeployPath() {
			return remoteDeployPath;
		}

		public void setRemoteDeployPath(String remoteDeployPath) {
			this.remoteDeployPath = remoteDeployPath;
		}

		public String getRemoteDataPath() {
			return remoteDataPath;
		}

		public void setRemoteDataPath(String remoteDataPath) {
			this.remoteDataPath = remoteDataPath;
		}
		
	}
	
	/*
	 * nosql 主机类
	 */
	
	public class NosqlHost extends Host{
		
		public NosqlHost(){}
		
		public NosqlHost(String jsonStr){
			try {
				JSONObject jsonObj = new JSONObject(jsonStr);
				setIp(jsonObj.optString("ip"));
				setPort(jsonObj.optInt("port"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
