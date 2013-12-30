package com.me.GCDP.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisCache {

	private static Log log = LogFactory.getLog(RedisCache.class);
	
	private JedisPool jedisPool;

	public void put(String key, Object value, int time) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value.toString());
			if (time > 0) {
				jedis.expire(key, time);
			}
			else
			{
				jedis.expire(key, time);
			}
		} catch (Exception e) {
			log.error("put data into redis " + e.getMessage());
		} finally {
			this.release(jedis);
		}
	}

	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			log.error("get data from redis " + e.getMessage());
			return null;
		} finally {
			this.release(jedis);
		}
	}

	public void remove(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			log.error("remove data from redis " + e.getMessage());
		} finally {
			this.release(jedis);
		}

	}

	public boolean contains(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.exists(key);
		} catch (Exception e) {
			log.error("read data from redis" + e.getMessage());
			return false;
		} finally {
			this.release(jedis);
		}

	}

	/*
	 * 释放资源
	 */
	private boolean release(Jedis jedis) {
		if (jedisPool != null && jedis != null) {
			jedisPool.returnResource(jedis);
			return true;
		}
		return false;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
}
