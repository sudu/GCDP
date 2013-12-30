package com.me.GCDP.util.oscache;

import java.util.Date;
import java.util.Properties;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :Hu WeiQi
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2011-7-18             Hu WeiQi               create the class     </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class OSCache extends GeneralCacheAdministrator {
	
	private static final long serialVersionUID = -4397192926052141162L;
	
	// 过期时间(单位为秒);
	private int refreshPeriod;
	
	// 关键字前缀字符;
	private String keyPrefix = "";

	public OSCache(Properties prop, String keyPrefix, int refreshPeriod) {
		super(prop);
		this.keyPrefix = keyPrefix;
		this.refreshPeriod = refreshPeriod;
	}

	// 添加被缓存的对象;
	public void put(String key, Object value) {
		this.putInCache(this.keyPrefix + "_" + key, value);
	}

	// 删除被缓存的对象;
	public void remove(String key) {
		this.flushEntry(this.keyPrefix + "_" + key);
	}

	// 删除所有被缓存的对象;
	public void removeAll(Date date) {
		this.flushAll(date);
	}

	public void removeAll() {
		this.flushAll();
	}

	// 获取被缓存的对象;
	public Object get(String key) {
		try {
			if (this.refreshPeriod <= 0) {
				return this.getFromCache(this.keyPrefix + "_" + key);
			} else {
				return this.getFromCache(this.keyPrefix + "_" + key, this.refreshPeriod);
			}
		} catch (NeedsRefreshException e) {
			this.cancelUpdate(this.keyPrefix + "_" + key);
			return null;
			//throw e;
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
}
