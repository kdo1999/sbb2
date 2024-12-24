package com.sbb2.common.config;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
	private final String redisHost;
	private final Integer redisPort;

	public RedisConfig(
		@Value("${spring.data.redis.host}") final String redisHost,
		@Value("${spring.data.redis.port}") final Integer redisPort
	) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory((redisConnectionFactory()));
		redisTemplate.setKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
		redisTemplate.setValueSerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
		redisTemplate.setHashKeySerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
		redisTemplate.setHashValueSerializer(new StringRedisSerializer(Charset.forName("UTF-8")));
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}
}
