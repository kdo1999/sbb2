package com.sbb2.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.redis.service.RedisService;

@Configuration
public class JwtConfig {
	@Value("${jwt.secret}")
	String secretKey;

	@Bean
	public JwtUtil jwtUtil(RedisService redisService) {
		return new JwtUtil(secretKey, redisService);
	}
}
