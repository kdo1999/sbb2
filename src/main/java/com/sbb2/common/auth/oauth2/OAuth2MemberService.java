package com.sbb2.common.auth.oauth2;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.LoginType;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;
import com.sbb2.member.exception.MemberBusinessLoginException;
import com.sbb2.member.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {
	private final List<OAuth2MemberInfoExtractor> OAuth2MemberInfoExtractors;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;


	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("oauth2MemberService 실행");

		final OAuth2User oAuth2User = super.loadUser(userRequest);

		Optional<OAuth2MemberInfoExtractor> oauth2MemberInfoExtractorOptional = OAuth2MemberInfoExtractors
			.stream()
			.filter(OAuth2MemberInfoExtractor -> OAuth2MemberInfoExtractor.accepts(userRequest))
			.findFirst();

		if (oauth2MemberInfoExtractorOptional.isEmpty()) {
			throw new InternalAuthenticationServiceException("해당 OAuth2 제공자는 아직 지원되지 않습니다");
		}

		CustomMemberDetails customMemberDetails = oauth2MemberInfoExtractorOptional.get().extractUserInfo(oAuth2User);

		try {
			//이미 가입된 회원이라면 아래 로직을 수행
			Member findMember = memberRepository.findByEmail(customMemberDetails.email())
					.orElseThrow(() -> new MemberBusinessLoginException(MemberErrorCode.NOT_FOUND));
			log.info("이메일 중복!");

			return customMemberDetails;
		} catch (MemberBusinessLoginException e) {
			//신규 가입하는 회원이라면 아래 로직 실행
			String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
			LoginType loginType = LoginType.from(registrationId);

			Member member = Member.builder()
				.email(customMemberDetails.email())
				.username(customMemberDetails.name())
				.memberRole(MemberRole.USER)
				.password(passwordEncoder.encode(createPassword()))
				.loginType(loginType)
				.build();

			memberRepository.save(member);

			return customMemberDetails;
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
}
