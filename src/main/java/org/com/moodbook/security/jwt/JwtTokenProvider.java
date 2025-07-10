package org.com.moodbook.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final SecretKey secretKey;// 토큰을 만들 때 서명하는 키

  //현재 로그인이 완료된 사용자 정보를 기반으로 access, refresh token 발급
  public String generateToken(Authentication authentication, Long expirationMillis) {

    //현재 로그인한 사용자의 정보를 꺼냄
    CustomMemberDetails customMemberDetails = (CustomMemberDetails) authentication.getPrincipal();
    Date expiryDate = new Date(new Date().getTime() + expirationMillis);//토큰 만료시간 생성(밀리초 단위까지)

    //payload
    Claims claims = Jwts.claims();
    claims.put("member-id", customMemberDetails.getId());//memberEntity의 pk값
    claims.put("email", customMemberDetails.getUsername());//member의 email값

    return Jwts.builder()
        .setSubject(customMemberDetails.getUsername())//이 JWT 토큰의 주체를 지정
        .setClaims(claims)//payload
        .setIssuedAt(new Date())//토큰 발급시간
        .setExpiration(expiryDate)//토큰 만료 시간
        .signWith(secretKey, SignatureAlgorithm.HS512)
        .compact();//위에서 저장한 정보들을 최종적으로 문자열로 만들어주는 메소드


  }

  //JWT 토큰에서 사용자 ID를 추출하는 메서드
  public Long getUserIdFromToken(String token) {
    return Jwts
        .parserBuilder()//JWT 토큰을 헤석하겠다고 선언
        .setSigningKey(secretKey)//토큰을 검증하기 위해 비밀키 사용
        .build()//해석할 준비완료
        .parseClaimsJws(token)//전달 받은 토큰을 파싱
        .getBody() //파싱한 토큰의 payload 부분을 꺼내서
        .get("member-id", Long.class);//member-id 를 반환
  }

  //토큰 유효성 검사
  public Boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (
        MalformedJwtException e) {
      //토큰 형식이 잘못되었을 때
      return false;
    } catch (
        ExpiredJwtException e) {
      //토큰이 만료가 되었을 때
      return false;
    } catch (
        UnsupportedJwtException e) {
      //지원하지 않는 토큰일 때
      return false;
    } catch (IllegalArgumentException e) {
      //토큰 문자열이 비어있거나 이상할 때
      return false;
    } catch (
        JwtException e) {
      //기타 예외
      return false;
    }
  }


}
