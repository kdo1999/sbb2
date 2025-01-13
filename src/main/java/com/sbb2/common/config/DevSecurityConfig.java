package com.sbb2.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.common.auth.filter.JwtFilter;
import com.sbb2.common.auth.oauth2.CustomResponseConverter;
import com.sbb2.common.auth.oauth2.OAuth2SuccessHandler;
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
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2UserService oAuth2UserService;
	private final PasswordEncoder passwordEncoder;

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
				.requestMatchers(HttpMethod.POST, "/api/v1/auth/code", "/api/v1/auth/password/reset")
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
			.oauth2Login(oauth2 -> {
				oauth2.userInfoEndpoint((userInfoConfig) -> userInfoConfig.userService(oAuth2UserService))
					.tokenEndpoint(token -> {
						token.accessTokenResponseClient(accessTokenResponseClient());
					})
					.successHandler(oAuth2SuccessHandler);
			})
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

	/**
	 * 인증 코드 인가 요청을 처리하기 위한 커스텀 OAuth2AccessTokenResponseClient를 구성합니다.
	 *
	 * @return 커스텀 설정이 적용된 OAuth2AccessTokenResponseClient
	 */
	@Bean
	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();

		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

		// 카카오 응답 값에서 refreshToken을 추출하기 위한 생성 및 커스텀 토큰 응답 컨버터 설정
		tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(new CustomResponseConverter());

		RestTemplate restTemplate = new RestTemplate(
			Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		accessTokenResponseClient.setRestOperations(restTemplate);
		return accessTokenResponseClient;
	}
}