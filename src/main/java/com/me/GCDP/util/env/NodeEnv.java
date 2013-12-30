package com.me.GCDP.util.env;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.me.GCDP.util.SpringContextUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.me.json.JSONArray;
import com.me.json.JSONException;
import com.me.json.JSONObject;

/**
 * 
 * <p>Title: </p>
 * <p>Description: 获取节点的相关信息</p>
 * <p>Company: ifeng.com</p>
 * @author :jiangy
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>上午10:27:34              jiangy              create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */
public class NodeEnv {

	private static NodeEnv nodeEnv = null;
	public static String json = null;
	
	private Map<Integer,Map<String,Object>> allMap = null;
	private static Log log = LogFactory.getLog(NodeEnv.class);
	public synchronized static NodeEnv getNodeEnvInstance(){
		if(nodeEnv == null){
			nodeEnv = new NodeEnv();
		}
		return nodeEnv;
	}
	/*从数据库里面重新读取信息
	 * */
	public void refresh(){
		getAllEnv();
	}
	private NodeEnv(){
		getAllEnv();
	}
	
	private ComboPooledDataSource dataSource = (ComboPooledDataSource)SpringContextUtil.getBean("dataSource");
	
	public Connection getConnection() throws SQLException
	{
		return (Connection) dataSource.getConnection();
	}
	private Map<Integer, Map<String, Object>> getAllEnv() {
		
		Connection conn = null;
		PreparedStatement preStmt = null;
//		try{Class.forName("com.mysql.jdbc.Driver");
//		DriverManager.setLoginTimeout(10);//连接超时时间
//		conn = DriverManager.getConnection("jdbc:mysql://192.168.15.12:3306/devDB", "developer","1234");
//		}catch(Exception e){}
		String sql = "select * from cmpp_node";
		ResultSet rs = null;
		try {
			conn = getConnection();
			preStmt = conn.prepareStatement(sql);
			rs = preStmt.executeQuery();
			allMap = new HashMap<Integer,Map<String,Object>>();
			while(rs.next()){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", rs.getInt(1));
				map.put("name",rs.getObject(2));
				if(rs.getObject(3)!=null){
					map.put("remote", getMap(rs.getObject(3).toString()));
				}
				if(rs.getObject(4)!=null){
					map.put("masterdb", getMap(rs.getObject(4).toString()));
				}
				if(rs.getObject(5)!=null){
					map.put("slavedb", getMap(rs.getObject(5).toString()));
				}
				if(rs.getObject(6)!=null){
					map.put("publicsearch", getList(rs.getObject(6).toString()));
				}
				if(rs.getObject(7)!=null){
					map.put("privatesearch", getList(rs.getObject(7).toString()));
				}
				if(rs.getObject(8)!=null){
					map.put("nosql", getMap(rs.getObject(8).toString()));//
				}
				if(rs.getObject(9)!=null){
					map.put("env", getList(rs.getObject(9).toString()));
				}
				allMap.put(rs.getInt(1), map);
			}
		} catch (SQLException e) {
			log.error("数据库连接异常："+e);
		}
		finally{
			try{
			if(rs!=null){
				rs.close();
				rs = null;
			}
			if(preStmt!=null){
				preStmt.close();
				preStmt = null;
			}
			if(conn!=null){
				conn.close();
			}
			}
			catch(SQLException e){
				log.error("数据库关闭异常："+e);
			}
		}
		
		return allMap;
	}
	/**
	 * 
	 * @Description: 
	 * @param nodeId 节点id
	 * @param keyValue key关键字的值
//	 * @return String 当前节点的值
//	 */
	@SuppressWarnings("rawtypes")
	public Map<String,Object> getEnvByKey(int nodeId,String keyValue){
		Map<String,Object> map = allMap.get(nodeId);
//		System.out.println(map);
		int mapIndex = -1;
		if(map!=null&&map.size()>0){
			List env = (List) map.get("env");
			int size = env.size();
			if(size>0){
//				out:for(int i = 0;i<size;i++){
//						Map envMap = (Map) env.get(i);
//						System.out.println(envMap.values());
//						Iterator envValues = envMap.values().iterator();
//						while(envValues.hasNext()){
//							if(key.equals(envValues.next())){
//								mapIndex = i;
//								System.out.println(mapIndex);
//								break out;
//							}
//						}
//				}
				if(keyValue!=null&&!keyValue.equals("")){
					for(int i = 0;i<size;i++){
						Map envMap = (Map) env.get(i);
						if(keyValue.equals(envMap.get("key"))){
							mapIndex = i;
							break;
						}
					}
				}
			}
			if(mapIndex>-1){
				return (Map) env.get(mapIndex);
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
			
	/**
	 * 
	 * @Description:将json转换成map，map下面装的是key-value类型
	 * @param jsonString
	 * @return   
	 * @return Map<String,Object>
	 */
	private Map<String, Object> getMap(String jsonString){
		JSONObject jsonObject;
		try
		{
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext())
			{
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		}
		catch (JSONException e)
		{
			log.error("JSON转换异常："+e);
		}
		return null;
	}

	/**
	 * 
	 * @Description:把json 转换为ArrayList 
	 * 形式ArrayList里面装的是map
	 * @param jsonString
	 * @return   
	 * @return List<Map<String,Object>>
	 */
	private List<Map<String, Object>> getList(String jsonString)
	{
		List<Map<String, Object>> list = null;
		try
		{
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject jsonObject;
			list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < jsonArray.length(); i++)
			{
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
		}
		catch (Exception e)
		{
			log.error("JSON转换异常："+e);
		}
		return list;
	}
//public static void main(String[] args){
//	System.out.println(NodeEnv.getNodeEnvInstance().getEnvByKey(2,"copyright"));
//}
	

}
