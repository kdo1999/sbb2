package com.sbb2.common.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.common.auth.filter.JwtFilter;
import com.sbb2.common.auth.service.MemberDetailsService;
import com.sbb2.common.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class DevSecurityConfig {
	private final ObjectMapper objectMapper;
	private final JwtUtil jwtUtil;
	private final MemberDetailsService memberDetailsService;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		//경로별 인가 작업
		httpSecurity
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(devCorsConfigurationSource()))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/auth/**")
				.permitAll()
				.requestMatchers("/", "/docs/**", "/error")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/question", "/api/v1/category")
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/question/{id}", "/api/v1/answer/{id}", "/api/v1/answer",
					"/api/v1/comment")
				.hasRole("USER")
				.requestMatchers(HttpMethod.POST, "/api/v1/question", "/api/v1/answer", "/api/v1/voter/{id}",
					"/api/v1/comment")
				.hasRole("USER")
				.requestMatchers(HttpMethod.PATCH, "/api/v1/question/{id}", "/api/v1/answer/{id}",
					"/api/v1/comment/{id}", "/api/v1/auth/password/change")
				.hasRole("USER")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/question/{id}", "/api/v1/answer/{id}",
					"/api/v1/voter/{id}", "/api/v1/comment/{id}")
				.hasRole("USER")
				.anyRequest()
				.authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		httpSecurity
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}

	@Bean
	CorsConfigurationSource devCorsConfigurationSource() {
		log.info("devCorsConfigurationSource 생성");
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(List.of("*"));
		corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
		corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setMaxAge(3600L);
		corsConfiguration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
		throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * ROLE Prefix 빈 값으로 변경
	 */
	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults("");
	}

	@Bean
	public AntPathMatcher antPathMatcher() {
		return new AntPathMatcher();
	}

	public OncePerRequestFilter jwtFilter() {
		JwtFilter jwtFilter = new JwtFilter(jwtUtil, objectMapper, memberDetailsService, antPathMatcher());

		jwtFilter
			.addUriPattern(HttpMethod.GET, "/api/v1/question/*", "/api/v1/answer/*", "/api/v1/answer",
				"/api/v1/comment")
			.addUriPattern(HttpMethod.POST, "/api/v1/question", "/api/v1/answer", "/api/v1/voter/*", "/api/v1/comment")
			.addUriPattern(HttpMethod.PATCH, "/api/v1/question/*", "/api/v1/answer/*", "/api/v1/comment/*",
				"/api/v1/auth/password/change")
			.addUriPattern(HttpMethod.DELETE, "/api/v1/question/*", "/api/v1/answer/*", "/api/v1/voter/*",
				"/api/v1/comment/*");
		return jwtFilter;
	}
}
