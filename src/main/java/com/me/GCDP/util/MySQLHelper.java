package com.me.GCDP.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.me.json.JSONArray;
import com.me.json.JSONException;

public class MySQLHelper {

	private static Log log = LogFactory.getLog(MySQLHelper.class);
	private static Connection getConnection()
	{
		ComboPooledDataSource dataSource = (ComboPooledDataSource) SpringContextUtil.getBean("dataSource");
		try {
			return (Connection) dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("获取数据库连接失败"+e.getMessage());
			return null;
		}
	}

	public static Vector<Map<String,Object>> ExecuteSql(String sql,Object[] parms) throws SQLException
	{
		logSql(sql,parms);
		Connection con = null;
		PreparedStatement preStmt = null;
		Vector<Map<String,Object>> rtn =null;
		try
		{
			con = getConnection();
			preStmt = getStatement(con,sql,parms);
			rtn = ExecuteSql(preStmt,"");
		}
		catch(SQLException ex)
		{
			log.error(ex.getMessage()+"sql="+sql);
			throw(ex);
		}
		finally
		{
			if(preStmt!=null)
				preStmt.close();
			if(con!=null)
				con.close();
		}
		return rtn;
	}
	private static Vector<Map<String,Object>> ExecuteSql(PreparedStatement preStmt,String sql) throws SQLException
	{
		Vector<Map<String,Object>> rtn = new Vector<Map<String,Object>>();
		ResultSet rs =(sql.equals("")?preStmt.executeQuery():preStmt.executeQuery(sql));
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		int cloumnCount = rsmd.getColumnCount();
		while(rs.next())
		{
			Map<String,Object> map = new HashMap<String, Object>();
			for(int k=0;k<cloumnCount;k++)
			{
				int index = k+1;
				Object o =  rs.getObject(index);
				if(o instanceof java.sql.Timestamp)
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time_str = sdf.format((Date)o);
					map.put(rsmd.getColumnLabel(index).toString(), time_str);
				}
				else
				{
					map.put(rsmd.getColumnLabel(index).toString(), o);
				}
			}
			rtn.add(map);
		}
		preStmt.close();
		return rtn;
	}
	private static PreparedStatement getStatement(Connection con,String sql,Object[] parms) throws SQLException
	{
		PreparedStatement pstmt = (PreparedStatement) con.prepareStatement(sql);
		if(parms!=null)
		{
			for(int i=0;i<parms.length;i++)
			{
				pstmt.setObject(i+1, parms[i]);
			}
		}
		return pstmt;
	}
	public static int[] executeBatch(List<String> sqls) throws SQLException
	{
		 Connection con=null;
		 Statement stm =null;
		try {
			 con = getConnection(); 
			 stm = con.createStatement();
			 for(String sql:sqls)
			 {
				 logSql(sql,null);
				 stm.addBatch(sql);
			 }
			 return stm.executeBatch();
		} catch (SQLException e) {
			log.error(e.getMessage()+"sqls="+sqls);
			throw e;
		}
		finally
		{
			if(stm!=null)
				stm.close();
			if(con!=null)
				con.close();
		}		
	}
	public static int ExecuteNoQuery(String sql,Object[] parms) throws SQLException
	{
		 Connection con=null;
		 PreparedStatement preStmt =null;
		 logSql(sql,parms);
		 int i = 0;
		try {
			 con = getConnection(); 
			preStmt = getStatement(con,sql,parms);
			i = preStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e.getMessage()+"sql="+sql);
			throw e;
		}
		finally
		{
			if(preStmt!=null)
				preStmt.close();
			if(con!=null)
				con.close();
		}
		
		return i;
	}
	
	public static String InsertAndReturnId(String sql,Object[] parms) throws SQLException{
		Connection con = null;
		PreparedStatement pstmt=null;
		logSql(sql,parms);
		String result="";
		try {
			con = getConnection();
			pstmt = (PreparedStatement) con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			if(parms!=null)
			{
				for(int i=0;i<parms.length;i++)
				{
					pstmt.setObject(i+1, parms[i]);
				}
			}
			pstmt.executeUpdate();
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			result=rs.getString(1);
		} 
		catch(SQLException ex)
		{
			log.error(ex.getMessage()+"sql="+sql);
			throw ex;
		}
		finally
		{
			if(pstmt!=null)
				pstmt.close();
			if(con!=null)
				con.close();
		}
		
		return result;
	}
	private static void logSql(String sql,Object parms)
	{
		String sp ="";
		try
		{
			sp = parms==null?"[]":new JSONArray(parms).toString();
		}
		catch(JSONException je)
		{
			log.error(je);
		}
		log.debug(sql+",params="+sp);
	}

}
