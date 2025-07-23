package org.com.moodbook.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

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

    //이메일로 기존 회원을 조회, 없으면 신규 회원으로 저장


  }
}
