package org.com.moodbook.oauth2.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.config.AppUrlProperties;
import org.com.moodbook.common.constants.AWSS3Constants;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.oauth2.dto.GoogleUserInfo;
import org.com.moodbook.oauth2.dto.KakaoUserInfo;
import org.com.moodbook.security.authentication.entity.AuthenticationEntity;
import org.com.moodbook.security.authentication.repository.AuthenticationRepository;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.com.moodbook.security.jwt.JwtProperties;
import org.com.moodbook.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final AuthenticationRepository authenticationRepository;
  private final AppUrlProperties appUrlProperties;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {

    DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
    Map<String, Object> attributes = oAuth2User.getAttributes();
    String registrationId = (String) attributes.get("registrationId");

    String email;
    String name  =  "";
    String profileImage;

    if("google".equals(registrationId)) {
      GoogleUserInfo userInfo = new GoogleUserInfo(attributes);
      email = userInfo.getEmail();
      name = userInfo.getName();
      profileImage = userInfo.getProfileImage();
    }else if ("kakao".equals(registrationId)) {
      KakaoUserInfo userInfo = new KakaoUserInfo(attributes);
      String nickname = userInfo.getNickname();
      String id = userInfo.getId();
      email = nickname+"_"+id+"@kakao.com";//이메일 생성
      profileImage = userInfo.getProfileImage();
      name = nickname;
    }else {
      log.warn("제공하지 않는 소셜로그인입니다: {}",registrationId);
      throw new BaseException(ErrorCode.UNSUPPORTED_PROVIDER);
    }



    Optional<Member> optionalMember = memberRepository.findByEmail(email);
    Member member;
    if (optionalMember.isPresent()) {
      member = optionalMember.get();
      member.setEmailVerified(true);
      memberRepository.save(member);
    } else {
      member = Member.builder()
          .email(email)
          .password("SOCIAL")
          .role(Role.USER)
          .name(name)
          .contact("SOCIAL")
          .emailVerified(true)
          .status(MemberStatus.ACTIVATED)
          .build();

      MemberProfile profile = MemberProfile.builder()
          .myImage(profileImage != null ? profileImage : AWSS3Constants.DEFAULT_PROFILE_IMAGE)
          .nickname(name)
          .gender(Gender.UNKNOWN)
          .address(" ")
          .build();

      member.setProfile(profile);
      memberRepository.save(member);
      log.info("신규 소셜 회원가입 완료: {}", email);
    }

    CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customMemberDetails,
        null,
        Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()))
    );
    SecurityContextHolder.getContext().setAuthentication(authToken);

    //토큰 발급
    String accessToken = jwtTokenProvider.generateToken(authToken, jwtProperties.getAccessTokenExpirationMs(), "access");
    log.info("accessToken: {}", accessToken);
    String refreshToken = jwtTokenProvider.generateToken(authToken, jwtProperties.getRefreshTokenExpirationMs(), "refresh");

    //refresh토큰 저장
    AuthenticationEntity entity = AuthenticationEntity.builder()
        .member(member)
        .refreshToken(refreshToken)
        .build();
    authenticationRepository.save(entity);


  }
}

