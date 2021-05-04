package com.yc.snacknet.redis.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisUtil {
	@Autowired
	private JedisPool jedisPool;

	public boolean setnx(String key, String val) {
		Jedis jedis = null;

		try {
			jedis = jedisPool.getResource();
			if (jedis == null) {
				return false;
			}

			if (jedis.setnx(key, val) > 0) {
				return true;
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return false;
	}
	
	public int delnx(String key, String val) {
		Jedis jedis = null;

		try {
			jedis = jedisPool.getResource();
			if (jedis == null) {
				return 0;
			}

			StringBuilder sbf = new StringBuilder();
			sbf.append("if redis.call('get', '").append(key).append("')").append("=='").append(val).append("'").
			append(" then ").append(" return redis.call('del','").append(key).append("')").
			append(" else ").append(" return 0").append(" end");
			return Integer.valueOf(jedis.eval(sbf.toString()).toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return 0;
	}
}
