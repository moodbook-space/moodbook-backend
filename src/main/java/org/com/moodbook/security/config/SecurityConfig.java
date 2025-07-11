package org.com.moodbook.security.config;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.security.jwt.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configurable//설정 클래스 등록
@EnableWebSecurity//스프링 시큐리티 활성화
@RequiredArgsConstructor//생성자 자동 생성
public class SecurityConfig {

  private final JwtTokenFilter jwtTokenFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}



}
