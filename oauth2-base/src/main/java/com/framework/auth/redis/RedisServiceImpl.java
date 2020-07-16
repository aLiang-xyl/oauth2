package com.framework.auth.redis;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * redis服务
 * 
 * @author xingyl
 * @date 2019年4月19日下午4:07:37
 */
public class RedisServiceImpl<K, V> implements RedisService<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private ValueOperations<K, V> mValueOperations;

	public RedisServiceImpl(RedisTemplate<K, V> mRedisTemplate) {
		mValueOperations = mRedisTemplate.opsForValue();
	}

	public void set(K key, V value) {
		mValueOperations.set(key, value);
	}

	public V get(K key) {
		return mValueOperations.get(key);
	}

	public void set(K key, V value, long timeout, TimeUnit unit) {
		mValueOperations.set(key, value, timeout, unit);
	}

	public void set(K key, V value, long timeout) {
		this.set(key, value, timeout, TimeUnit.SECONDS);
	}

	public void delete(K key) {
		mValueOperations.getOperations().delete(key);
	}

	public Set<K> keys(K pattern) {
		return mValueOperations.getOperations().keys(pattern);
	}

	public boolean setIfAbsent(K key, V value) {
		return mValueOperations.setIfPresent(key, value);
	}

	public boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit) {
		return mValueOperations.setIfAbsent(key, value, timeout, unit);
	}
}
