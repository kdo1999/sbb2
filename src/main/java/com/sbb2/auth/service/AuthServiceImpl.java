package com.sbb2.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.auth.domain.EmailCertification;
import com.sbb2.auth.domain.VerifyType;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.exception.AuthBusinessLogicException;
import com.sbb2.common.auth.exception.AuthErrorCode;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.mail.service.MailService;
import com.sbb2.common.mail.util.TemplateName;
import com.sbb2.common.redis.service.RedisService;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.LoginType;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;
import com.sbb2.member.exception.MemberBusinessLoginException;
import com.sbb2.member.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
	private static final String REDIS_EMAIL_PREFIX = "certification_email:";
	private static final Long TEN_MINUTES_MILLIS = (long)10 * 60 * 1000;

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final AuthenticationManager authenticationManager;
	private final MailService mailService;
	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	@Override
	public MemberEmailSignupResponse signup(String email, String username, String password, LoginType loginType) {
		existsMember(email, username);

		Member.MemberBuilder memberBuilder = Member.builder()
			.email(email)
			.username(username)
			.memberRole(MemberRole.USER)
			.loginType(loginType);

		switch (loginType) {
			case EMAIL -> memberBuilder.password(passwordEncoder.encode(password));
			case KAKAO, NAVER -> memberBuilder.password(passwordEncoder.encode(createPassword()));
			default -> throw new AuthBusinessLogicException(AuthErrorCode.LOGIN_TYPE_NOT_SUPPORT);
		}

		Member savedMember = memberRepository.save(memberBuilder.build());

		return MemberEmailSignupResponse.builder()
			.email(savedMember.email())
			.username(savedMember.username())
			.memberRole(savedMember.memberRole())
			.build();
	}

	@Transactional(readOnly = true)
	@Override
	public MemberLoginResponse memberLogin(String email, String password) {
		Authentication authentication = authenticationManager.authenticate(
			new MemberLoginToken(email, password)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		MemberUserDetails memberUserDetails = (MemberUserDetails)authentication.getPrincipal();

		Member member = memberUserDetails.getMember();
		return new MemberLoginResponse(
			member.email(), member.username(),
			member.memberRole()
		);
	}

	@Override
	public void passwordChange(String originalPassword, String changePassword, Member loginMember) {
		if (!LoginType.EMAIL.equals(loginMember.loginType())) {
			throw new AuthBusinessLogicException(AuthErrorCode.LOGIN_TYPE_NOT_EMAIL);
		}

		if (!passwordEncoder.matches(originalPassword, loginMember.password())) {
			throw new AuthBusinessLogicException(AuthErrorCode.PASSWORD_NOT_MATCH);
		}

		Member changedPasswordMember = loginMember.changePassword(passwordEncoder.encode(changePassword));

		memberRepository.save(changedPasswordMember);
	}

	@Override
	public void passwordReset(String email, String certificationCode, VerifyType verifyType) {
		Map<?, ?> findHashDataAll = redisService.getHashDataAll(REDIS_EMAIL_PREFIX + email);

		//Redis에 인증코드 정보가 존재하는지 검증
		if (findHashDataAll.isEmpty()) {
			throw new AuthBusinessLogicException(AuthErrorCode.CERTIFICATION_CODE_NOT_FOUND);
		}

		EmailCertification findEmailCertification = objectMapper.convertValue(findHashDataAll,
			EmailCertification.class);

		//인증 타입 검증
		if (!findEmailCertification.getVerifyType().equalsIgnoreCase(verifyType.toString())) {
			throw new AuthBusinessLogicException(AuthErrorCode.VERIFY_TYPE_NOT_MATCH);
		}

		//인증 코드 일치하는지 검증
		if (!findEmailCertification.getCertificationCode().equals(certificationCode)) {
			throw new AuthBusinessLogicException(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH);
		}

		Member findMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberBusinessLoginException(MemberErrorCode.NOT_FOUND));

		Map<String, String> htmlParameterMap = new HashMap<>();
		String temporaryPassword = createPassword();

		Member changePasswordMember = findMember.changePassword(passwordEncoder.encode(temporaryPassword));

		memberRepository.save(changePasswordMember);

		htmlParameterMap.put("temporaryPassword", temporaryPassword);

		mailService.sendEmail(findMember.email(), htmlParameterMap, TemplateName.PASSWORD_RESET);
	}

	@Override
	public void sendCode(String email, VerifyType verifyType) {
		Member findMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberBusinessLoginException(MemberErrorCode.NOT_FOUND));

		if (!LoginType.EMAIL.equals(findMember.loginType())) {
			throw new AuthBusinessLogicException(AuthErrorCode.LOGIN_TYPE_NOT_EMAIL);
		}

		String certificationCode = UUID.randomUUID().toString();

		Map<?, ?> findHashDataAll = redisService.getHashDataAll(REDIS_EMAIL_PREFIX + email);
		EmailCertification findEmailCertification = objectMapper.convertValue(findHashDataAll,
			EmailCertification.class);

		if (findHashDataAll.isEmpty()) {
			EmailCertification emailCertification = EmailCertification.builder()
				.certificationCode(certificationCode)
				.sendCount("1")
				.verifyType(verifyType.toString())
				.build();

			sendCertificationMail(email, emailCertification, verifyType);
		} else if (verifyType.toString().equalsIgnoreCase(findEmailCertification.getVerifyType())) {
			//5회 이상 요청했을 때 EXCEPTION
			if (Integer.parseInt(findEmailCertification.getSendCount()) > 5) {
				throw new AuthBusinessLogicException(AuthErrorCode.TOOMANY_RESEND_ATTEMPTS);
			} else {
				findEmailCertification.addResendCount();
				findEmailCertification.setCertificationCode(certificationCode);

				sendCertificationMail(email, findEmailCertification, verifyType);
			}
		} else {
			throw new AuthBusinessLogicException(AuthErrorCode.UNKNOWN_SERVER);
		}
	}

	private void sendCertificationMail(String email, EmailCertification emailCertification, VerifyType verifyType) {
		redisService.setHashDataAll(
			REDIS_EMAIL_PREFIX + email, objectMapper.convertValue(emailCertification, Map.class)
		);

		redisService.setTimeout(REDIS_EMAIL_PREFIX + email, TEN_MINUTES_MILLIS);

		switch (verifyType) {
			case PASSWORD_RESET_VERIFY -> {
				Map<String, String> htmlParameterMap = new HashMap<>();
				htmlParameterMap.put("certificationCode", emailCertification.getCertificationCode());
				mailService.sendEmail(email, htmlParameterMap, TemplateName.PASSWORD_RESET_VERIFY);
				break;
			}
		}
	}

	private String createPassword() {
		final String SPECIAL_CHARACTERS = "~!@#$%^&*+=()_-";
		String uuid = UUID.randomUUID().toString().replace("-", "");
		StringBuilder password = new StringBuilder();

		char randomLetter = (char)('a' + ThreadLocalRandom.current().nextInt(26));
		password.append(randomLetter);

		char randomSpecialChar = SPECIAL_CHARACTERS.charAt(
			ThreadLocalRandom.current().nextInt(SPECIAL_CHARACTERS.length()));
		password.append(randomSpecialChar);

		char randomDigit = (char)('0' + ThreadLocalRandom.current().nextInt(10));
		password.append(randomDigit);

		while (password.length() < 8) {
			char randomChar = uuid.charAt(ThreadLocalRandom.current().nextInt(uuid.length()));
			password.append(randomChar);
		}

		return password.toString();
	}

	private void existsMember(String email, String username) {
		Boolean emailExists = memberRepository.existsByEmail(email);
		Boolean usernameExists = memberRepository.existsByUsername(username);

		if (emailExists) {
			throw new MemberBusinessLoginException(MemberErrorCode.EXISTS_EMAIL);
		}

		if (usernameExists) {
			throw new MemberBusinessLoginException(MemberErrorCode.EXISTS_USERNAME);
		}
	}
}

