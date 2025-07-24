package org.com.moodbook.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.constants.AWSS3Constants;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberRepository;
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

  //OAuth2 로그인 성공 시 자동으로 호출되는 메서드
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication)
      throws IOException, ServletException {
    //인증 된 사용자 정보 꺼내기 (구글에서 제공된 사용자 정보)
    DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
    // 이메일과 이름은 대부분 OAuth2에서 제공함
    Map<String,Object> attributes = oAuth2User.getAttributes();

    //이메일과 이름은 대부분 OAuth2에서 제공함
    String email = (String) attributes.get("email");
    String name = (String) attributes.get("name");

    // 3. DB에서 회원 조회 (소셜 로그인은 이메일 기준으로 식별)
    Optional<Member> optionalMember = memberRepository.findByEmail(email);

    Member member;
    if (optionalMember.isPresent()) {
      //이미 존재하는 경우 -> 병합 로직 수행
      member = optionalMember.get();
      member.setEmailVerified(true);//이미 인증 된이메일로 간주
      memberRepository.save(member);
    }else {
      // 존재하지 않는 경우 새로운 회원 및 프로필 등록
      member = Member.builder()
          .email(email)
          .password("SOCIAL")//소셜로그인 계정은 비밀번호 없음
          .role(Role.USER)
          .name(name)
          .contact("SOCIAL")
          .emailVerified(true)
          .status(MemberStatus.ACTIVATED)
          .build();

      MemberProfile profile = MemberProfile.builder()
          .myImage(AWSS3Constants.DEFAULT_PROFILE_IMAGE)
          .nickname(name)
          .gender(Gender.UNKNOWN)
          .address(" ")
          .build();

      member.setProfile(profile);
      memberRepository.save(member);
      log.info("소셜 회원가입 성공: {}", email);
    }
    CustomMemberDetails customMemberDetails = new CustomMemberDetails(member);
    // 2.토큰 발급
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        customMemberDetails,
        null,
        Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()))
    );
    SecurityContextHolder.getContext().setAuthentication(authToken);
    // 3.access,refresh 토큰 생성
    String accessToken = jwtTokenProvider.generateToken(authToken,jwtProperties.getAccessTokenExpirationMs() ,"access");
    String refreshToken = jwtTokenProvider.generateToken(authToken,jwtProperties.getRefreshTokenExpirationMs(),"refresh");

    // 4.refreshToken 저장
    AuthenticationEntity entity = AuthenticationEntity.builder()
        .member(member)
        .refreshToken(refreshToken)
        .build();
    authenticationRepository.save(entity);

    // 5. accessToken은 local스토리지에 보내기 위해 쿼리 파라미터로 전달
    response.sendRedirect("http://localhost:3000/main?access_token=" + accessToken +"&id=" + member.getId());


  }


}
