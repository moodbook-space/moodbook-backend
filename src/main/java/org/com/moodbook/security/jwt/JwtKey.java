package org.com.moodbook.security.jwt;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtKey {

  @Value("${jwt.secretKey}")
  private String secretKey;

  @Bean
  public SecretKey secretKey() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);//설정파일에서 불러온 키 값을 바이트로 배열로 변환
    return new SecretKeySpec(keyBytes, "HmacSHA512"); //바이트 배열을 HmacSHA256용 Security 객체로 매핑
  }
}
