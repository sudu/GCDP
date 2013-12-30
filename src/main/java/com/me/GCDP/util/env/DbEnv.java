package com.me.GCDP.util.env;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.me.GCDP.util.MySQLHelper;

public class DbEnv {
	private static DbEnv instance;
	private static Map<String,Integer> MaxBakIdTab = new HashMap<String,Integer>();
	private static Object lock = new Object();
	//private static Log log = LogFactory.getLog(DbEnv.class);
	public static DbEnv getInstance()
	{
		if (instance == null){  
            synchronized( lock ){  
                if (instance == null){  
                    instance = new DbEnv();  
                }  
            }
		}
		return instance;
	}
	public Integer getMaxBakId(String tableName) throws SQLException
	{
		Integer rtn=MaxBakIdTab.get(tableName);
		if(rtn==null)
		{
			String sql = "select max(id) id from "+tableName;
			Vector<Map<String,Object>> data = MySQLHelper.ExecuteSql(sql, null);
			if(data.size()>0)
			{
				rtn= (Integer) data.get(0).get("id");
				if(rtn != null){
					setMaxBakId(tableName, rtn);
					return rtn;
				}else{
					return 0;
				}
			}
			return 0;
		}
		else
		{
			return rtn;
		}
	}
	public void setMaxBakId(String tableName,Integer id)
	{
		MaxBakIdTab.put(tableName, id);
	}

}
