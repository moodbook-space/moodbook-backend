package org.com.moodbook.oauth2.service;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.oauth2.dto.GoogleUserInfo;
import org.com.moodbook.oauth2.dto.KakaoUserInfo;
import org.com.moodbook.oauth2.dto.OAuth2UserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    log.info("소셜 로그인 성공 - 제공자: {}", registrationId);

    OAuth2UserInfo userInfo;
    Map<String, Object> enrichedAttributes;

    if ("google".equals(registrationId)) {
      userInfo = new GoogleUserInfo(oAuth2User.getAttributes());
      log.info("구글 이메일: {}", ((GoogleUserInfo) userInfo).getEmail());

      enrichedAttributes = new HashMap<>(userInfo.getAttributes());
      enrichedAttributes.put("registrationId", registrationId);
      enrichedAttributes.put("email", ((GoogleUserInfo) userInfo).getEmail());

      return new DefaultOAuth2User(
          java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")),
          enrichedAttributes,
          "email"
      );

    } else if ("kakao".equals(registrationId)) {
      userInfo = new KakaoUserInfo(oAuth2User.getAttributes());
      log.info("카카오 닉네임: {}, ID: {}", userInfo.getName(), userInfo.getId());

      enrichedAttributes = new HashMap<>(userInfo.getAttributes());
      enrichedAttributes.put("registrationId", registrationId);

      return new DefaultOAuth2User(
          java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")),
          enrichedAttributes,
          "registrationId"  // 아무거나 key 하나 지정 (우리가 직접 email 생성 예정)
      );
    }

    throw new BaseException(ErrorCode.UNSUPPORTED_PROVIDER);
  }
}