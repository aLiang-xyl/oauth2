package com.framework.auth.redis;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 描述: redis服务类
 * </p>
 * 
 * @author xingyl
 * @date 2019年11月28日 下午7:58:49
 */
public interface RedisService<K, V> {

	public void set(K key, V value);

	public V get(K key);

	public void set(K key, V value, long timeout, TimeUnit unit);

	public void set(K key, V value, long timeout);

	public void delete(K key);

	public Set<K> keys(K pattern);

	public boolean setIfAbsent(K key, V value);

	public boolean setIfAbsent(K key, V value, long timeout, TimeUnit unit);

}
