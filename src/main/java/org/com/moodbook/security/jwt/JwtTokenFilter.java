package org.com.moodbook.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.threadlocal.TraceIdHolder;
import org.hibernate.annotations.Filter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  //HTTP 매 요청마다 호출
  @Override
  protected void doFilterInternal(HttpServletRequest request, //http요청
                                  HttpServletResponse response, //http응답
                                  FilterChain filterChain
  ) throws ServletException, IOException {

    try {
      //HTTP 요청이 시작되는 구간에서 TraceID 발급
      String traceId = UUID.randomUUID().toString().substring(0, 8);
      TraceIdHolder.set(traceId); //TraceID ThreadLocal에 저장

      String accessToken = getTokenFromRequest(request);//요청 헤더에서 토큰 추출

      if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(accessToken);
        //토큰에서 사용자를 꺼내서 담은 사용자 인증 객체
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //http요청으로부터 부가 정보(ip,세션 등)를 추출해서 사용자 인증 객체에 넣어줌
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String url = request.getRequestURL().toString();
        String method = request.getMethod();//GET,POST,PUT
      } else {

      }
      filterChain.doFilter(request,response); // JwtTokenFilter를 거치고 다음 필터로 넘어감

    }
    finally {
      //HTTP 요청이 끝날 때 ThreadLocal 데이터를 비워줌
      TraceIdHolder.clear();
      String afterClear = TraceIdHolder.get();
    }

  }
  //HTTP 요청 헤더에서 토큰을 추출하는 메서드
  public String getTokenFromRequest(HttpServletRequest request) {

    String token = null;

    String bearerToken = request.getHeader("Authorization");
    if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      token = bearerToken.substring(7);
    }
    return token;

  }

  //http 요청에서 사용자 인증 정보를 담는 객체
  private UsernamePasswordAuthenticationToken getAuthentication(String token) {
    Long memberId = jwtTokenProvider.getUserIdFromToken(token);

    return null;
  }


}
