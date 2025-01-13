package com.sbb2.common.jwt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.jwt.exception.JwtTokenBusinessLogicException;
import com.sbb2.common.jwt.exception.JwtTokenErrorCode;
import com.sbb2.common.redis.service.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 관련 로직을 처리하는 클래스 입니다.
 *
 * @author : Kim Dong O
 * @fileName : JwtUtil
 * @since : 05/14/24
 */
@Slf4j
@Transactional(readOnly = true)
public class JwtUtil {
	private static final String REDIS_JWT_PREFIX = "jwt_member:";
	private static final String REDIS_BLACK_VALUE = "black";
	private final SecretKey secretKey;
	private final RedisService redisService;

	public JwtUtil(String secretKey,
		RedisService redisService) {
		this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.redisService = redisService;
	}

	/**
	 * HttpServletRequest를 담아서 보내면 AccessToken 반환
	 * @throws JwtTokenBusinessLogicException 토큰이 존재하지 않을때 예외 발생
	 * @param request
	 * @return String
	 */
	public String getAccessToken(HttpServletRequest request) {
		String access = null;
		Cookie[] cookies = request.getCookies();

		//cookies null이 아니라면
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(TokenType.ACCESS.toString())) {
					access = cookie.getValue();
				}
			}
		}

		if (!StringUtils.hasText(access)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.MALFORMED);
		}

		return access;
	}

	/**
	 * HttpServletRequset를 담아서 보내면 RefreshToken 반환
	 *
	 * @param request
	 * @throws JwtTokenBusinessLogicException 쿠키에 refreshToken이 존재하지 않을때 예외 발생
	 * @return String
	 */
	public String getRefreshToken(HttpServletRequest request) throws MalformedJwtException {
		String refresh = null;
		Cookie[] cookies = request.getCookies();

		//cookies null이 아니라면
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(TokenType.REFRESH)) {
					refresh = cookie.getValue();
				}
			}
		}

		if (!StringUtils.hasText(refresh)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.MALFORMED);
		}

		return refresh;
	}

	public String getNickname(String token) {
		return getPayload(token).get("nickname", String.class);
	}

	/**
	 * 토큰 내 CategoryEntity 값 반환
	 *
	 * @param token
	 * @return String
	 */
	public String getCategory(String token) {
		return getPayload(token).get("category", String.class);
	}

	/**
	 * 토큰 내 memberEmail 값 반환
	 *
	 * @param token
	 * @return String
	 */
	public String getEmail(String token) {
		return getPayload(token).get("email", String.class);
	}

	/**
	 * 토큰 내 memberRole 값 반환
	 *
	 * @param token
	 * @return String
	 */
	public String getRole(String token) {
		return getPayload(token).get("role", String.class);
	}

	/**
	 * 토큰 내 memberStatus 값 반환
	 *
	 * @param token
	 * @return String
	 */
	public String getMemberStatus(String token) {
		return getPayload(token).get("memberStatus", String.class);
	}

	/**
	 * 남은 만료시간을 반환해주는 메소드
	 * @param token
	 * @return long (millsSecond)
	 */
	public long getExpiration(String token) {
		Date expiration = getPayload(token).getExpiration();
		Date now = toDate(createIssuedAt());
		return expiration.getTime() - now.getTime();
	}

	/**
	 * 토큰 담아서 보낼시 Payload 반환
	 *
	 * @param token
	 * @return Claims
	 */
	private Claims getPayload(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}

	public boolean isAccessToken(String token) {
		return getCategory(token).equals(TokenType.ACCESS.toString());
	}

	/**
	 * 만료시간 검증<br>
	 * 만료시간이 남아있으면 true, 만료시간보다 지났으면 false
	 *
	 * @param token
	 * @return boolean
	 */
	public boolean isExpired(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(toDate(createIssuedAt()));
		} catch (ExpiredJwtException e) {
			return false;
		} catch (SignatureException e) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.SIGNATURE);
		}
		return true;
	}

	public boolean isRefreshToken(String token) {
		return getCategory(token).equals(TokenType.REFRESH);
	}

	/**
	 * MemberLoginSuccessDto 담아서 보내면 AccessToken 반환
	 *
	 * @param memberLoginResponse
	 * @return
	 */
	public String createAccessToken(MemberLoginResponse memberLoginResponse) {
		Map<String, String> map = new HashMap<>();

		map.put("category", TokenType.ACCESS.toString());
		map.put("username", memberLoginResponse.username());
		map.put("email", memberLoginResponse.email());
		map.put("memberRole", memberLoginResponse.memberRole().toString());

		LocalDateTime issuedAt = createIssuedAt();
		LocalDateTime expiration = issuedAt.plusMinutes(10L);

		return Jwts.builder()
			.claims(map)
			.issuedAt(toDate(issuedAt))
			.expiration(toDate(expiration))
			.signWith(secretKey)
			.compact();
	}

	/**
	 * MemberLoginSuccessDto 담아서 보내면 RefreshToken 반환
	 *
	 * @param memberLoginSuccessDto
	 * @return
	 */
	@Transactional
	public String createRefreshToken(MemberLoginResponse memberLoginSuccessDto) {
		Map<String, String> map = new HashMap<>();

		map.put("category", TokenType.REFRESH.toString());
		map.put("email", memberLoginSuccessDto.email().toString());

		LocalDateTime issuedAt = createIssuedAt();
		LocalDateTime expiration = issuedAt.plusHours(24L);

		String refreshToken = Jwts.builder()
			.claims(map)
			.issuedAt(toDate(issuedAt))
			.expiration(toDate(expiration))
			.signWith(secretKey)
			.compact();

		redisService.setData(REDIS_JWT_PREFIX + memberLoginSuccessDto.email(), refreshToken, 24 * 60 * 60 * 1000); //1일

		return refreshToken;
	}

	protected LocalDateTime createIssuedAt() {
		return LocalDateTime.now(ZoneId.systemDefault());
	}

	/**
	 * RefreshToken 검증 로직
	 *
	 * @param request
	 * @return refreshToken
	 * @throws JwtTokenBusinessLogicException <br>
	 * RefreshToken의 만료 시간이 지났을 때 Exception <br>
	 * RefreshToken이 존재하지 않으면 Exception <br>
	 * RefreshToken category가 refresh가 아닐시 Exception <br>
	 * Redis에 저장된 토큰 값과 일치하지 않을시 Exception <br>
	 */
	public String refreshTokenValid(HttpServletRequest request) {
		String refreshToken = getRefreshToken(request);

		// refreshToken이 존재하는지 검증
		if (refreshToken == null) {
			//exception
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.MALFORMED);
		}

		//만료시간 검증
		if (!isExpired(refreshToken)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.EXPIRED);
		}

		// RefreshToken이 맞는지 검증
		if (!isRefreshToken(refreshToken)) {
			//exception
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.SIGNATURE);
		}

		//Redis에 저장된 RefreshToken이랑 값이 같은지 검증
		if (!refreshToken.equals(redisService.getData(REDIS_JWT_PREFIX + getEmail(refreshToken)))) {
			//exception
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.NOT_VALID);
		}

		return refreshToken;
	}

	/***
	 * 토큰 유효기간동안 BlackList에 추가
	 * @param token
	 */
	@Transactional
	public void addTokenBlacklist(String token) {
		redisService.setData(token, REDIS_BLACK_VALUE, getExpiration(token));
	}

	/**
	 * 로그아웃 로직
	 * @exception JwtTokenBusinessLogicException <br>
	 * - AccessToken이 아닐때 예외 발생 <br>
	 * - RefreshToken이 아닐 때 예외 발생 <br>
	 * - Redis에 저장된 토큰과 일치하지 않을때 예외 발생
	 * @param accessToken
	 * @param refreshToken
	 */
	@Transactional
	public void logout(String accessToken, String refreshToken) {
		//유효시간이 남아있다면 redis에 유효시간만큼 저장
		if (isExpired(accessToken)) {
			//AccessToken이 아니라면 Exception
			if (!isAccessToken(accessToken)) {
				throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.INVALID_ACCESS_TOKEN);
			}
			addTokenBlacklist(accessToken);
		}

		//RefreshToken이 아니라면 Exception
		if (!isRefreshToken(refreshToken)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.SIGNATURE);
		}

		//Redis에 저장된 RefreshToken이랑 값이 같은지 검증
		if (!refreshToken.equals(redisService.getData(REDIS_JWT_PREFIX + getEmail(refreshToken)))) {
			//exception
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.NOT_VALID);
		}

		//refresh토큰 삭제
		redisService.delete(REDIS_JWT_PREFIX + getEmail(refreshToken));
	}

	/**
	 * LocalDateTime값을 Date로 변환해주는 메소드
	 *
	 * @param localDateTime
	 * @return Date
	 */
	protected Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 토큰이 블랙리스트에 존재하는지 체크하는 메소드
	 * @throws JwtTokenBusinessLogicException 토큰이 블랙리스트에 존재하는경우 예외 발생
	 * @param token
	 */
	public void blackListCheck(String token) {
		if (REDIS_BLACK_VALUE.equals(redisService.getData(token))) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.NOT_VERIFIED);
		}
	}
}
