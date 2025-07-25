package org.com.moodbook.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.com.moodbook.security.core.CustomUserDetailsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final RedisTemplate<String, String> redisTemplate;


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();


    // 정적 파일 경로, 필터
    return path.startsWith("/css/")
        || path.startsWith("/js/")
        || path.startsWith("/images/")
        || path.equals("/")
        || path.equals("/index.html")
        || path.equals("/login.html")
        || path.equals("/signUp.html")

        || path.endsWith(".html")
        || path.startsWith("/favicon.ico")
        || path.startsWith("/api/oauth/sighUp")
        || path.startsWith("/api/oauth/login");

    /**
     config에서 인증하라고 하고 여기서 인증 무시하라고 하면 401 에러남
     **/
  }

  //HTTP 매 요청마다 호출
  @Override
  protected void doFilterInternal(HttpServletRequest request,         // http 요청
      HttpServletResponse response,           // http 응답
      FilterChain filterChain
  ) throws ServletException, IOException {

    try {
      String accessToken = extractTokenFromRequest(request);      // 요청 헤더에서 토큰 추출

      //블랙 리스트 확인
      if (StringUtils.hasText(accessToken)) {
        String blacklistKey = "access-token-blacklist:" + accessToken;
        if (redisTemplate.hasKey(blacklistKey)) {
          // 로그아웃 된 토큰이므로 인증 차단
          log.warn("해당 토큰은 사용이 불가능합니다: {}", accessToken);
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("이미 로그아웃 된 토큰입니다");
          return;
        }
      }
      //토큰 유효성 검사 및 인증 처리

      if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(
            accessToken);
        if(authenticationToken != null) {
          CustomMemberDetails memberDetails = (CustomMemberDetails) authenticationToken.getPrincipal();

          //상태 확인: 비활성화 된 회원이면 403응답 반환
          if (memberDetails.getStatus() == MemberStatus.DEACTIVATED){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("비활성화 된 계정입니다");
            return;

          }
        }
        // 토큰에서 사용자를 꺼내서 담은 사용자 인증 객체

        authenticationToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        // http 요청으로부터 부가 정보(ip, 세션 등)를 추출해서 사용자 인증 객체에 넣어줌

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 토큰에서 사용자 인증정보를 조회해서 인증정보를 현재 스레드에 인증된 사용자로 등록

        String url = request.getRequestURI().toString();
        log.info("현재 들어온 HTTP 요청 = " + url);

        String method = request.getMethod();                // GET, POST, PUT ...
        log.info("HTTP 메소드 + method = " + method);
      }

      /**
       * CharacterEncodingFilter: 문자 인코딩 처리
       * CorsFilter: CORS 정책 처리
       * CsrfFilter: CSRF 보안 처리
       * JWTTokenFilter: JWT 토큰 처리(핵심)
       * SecurityContextFilter: 인증/인가 정보 저장
       * ExceptionFilter: 예외 처리
       */

      filterChain.doFilter(request, response);        // JwtTokenFilter를 거치고 다음 필터로 넘어감 (이동...이동...이동)
    } finally {

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
    //1. 토큰에서 사용자 ID 추출
    Long memberId = jwtTokenProvider.getUserIdFromToken(token);

    if (memberId == null) {
      return null;//memberId 와 일치하는게 없다면 유효하지 않은 토큰이기 때문에 null 반환
    }
    //2.사용자 Id로 DB 조회.
    CustomMemberDetails memberDetails =
        (CustomMemberDetails) customUserDetailsService.loadUserById(memberId);

    return new UsernamePasswordAuthenticationToken(
        memberDetails,
        null,
        memberDetails.getAuthorities()
    );
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    // 1. 쿠키에서 accessToken 확인
    String cookieToken = extractTokenFromCookie(request);
    if (StringUtils.hasText(cookieToken)) {
      return cookieToken;
    }

    // 2. Authorization 헤더에서 토큰 확인
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // "Bearer " 제거
    }

    return null;
  }
  // 쿠키에서 accessToken을 추출하는 메서드
  private String extractTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("accessToken")) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }



}
