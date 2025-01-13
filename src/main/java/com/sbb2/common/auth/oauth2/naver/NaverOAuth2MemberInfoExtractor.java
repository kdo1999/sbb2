package com.sbb2.common.auth.oauth2.naver;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sbb2.common.auth.oauth2.CustomMemberDetails;
import com.sbb2.common.auth.oauth2.OAuth2MemberInfoExtractor;
import com.sbb2.common.auth.oauth2.OAuth2Provider;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class NaverOAuth2MemberInfoExtractor implements OAuth2MemberInfoExtractor {
	@Override
	public CustomMemberDetails extractUserInfo(OAuth2User oauth2User) {
		log.info("OAuth2User : {}", oauth2User);
		Map<String, Object> attributes = oauth2User.getAttributes();

		Long id = (Long)attributes.get("id");

		Map<String, Object> properties = (Map<String, Object>)attributes.get("response");
		log.info("properties : {}", properties);
		String nickname = properties != null ? (String)properties.get("nickname") : "";
		String profileImage = properties != null ? (String)properties.get("profile_image") : "";
		String email = properties != null ? (String)properties.get("email") : "";

		Collection<? extends GrantedAuthority> authorities = oauth2User.getAuthorities();

		return new CustomMemberDetails(
			id,
			nickname,
			nickname,
			email,
			profileImage,
			OAuth2Provider.NAVER,
			authorities,
			attributes
		);
	}

	@Override
	public boolean accepts(OAuth2UserRequest userRequest) {
		return OAuth2Provider.NAVER.name().equalsIgnoreCase(userRequest.getClientRegistration().getRegistrationId());
	}
}
