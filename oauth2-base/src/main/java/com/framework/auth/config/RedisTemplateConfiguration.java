package com.framework.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.framework.auth.redis.RedisService;
import com.framework.auth.redis.RedisServiceImpl;

/**
 * <p>描述: redis模板配置</p>
 * 
 * @author xingyl
 * @date 2019年11月28日 下午6:48:22
 */
@Configuration
public class RedisTemplateConfiguration {

	/**
	 * redis模板配置
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
	
	@Bean
	public <K, V> RedisService<K, V> redisService(@Qualifier("redisTemplate") RedisTemplate<K, V> mRedisTemplate) {
		return new RedisServiceImpl<>(mRedisTemplate);
	}
}
