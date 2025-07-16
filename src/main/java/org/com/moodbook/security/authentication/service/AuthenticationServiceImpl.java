package org.com.moodbook.security.authentication.service;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.security.authentication.entity.AuthenticationEntity;
import org.com.moodbook.security.authentication.repository.AuthenticationRepository;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.com.moodbook.security.jwt.JwtPropertires;
import org.com.moodbook.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtPropertires jwtPropertires;
  private final AuthenticationRepository authenticationReposiroty;


  @Override
  public LoginResponseDTO issueTokenAndStore(MemberDTO memberDTO) {
    Member member = memberDTO.toEntity();
    //AuthenticationEntity 객체 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(new CustomMemberDetails(member),
        null,
        Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
    //2. Jwt토큰 생성
    String accessToken = jwtTokenProvider.generateToken(authToken,jwtPropertires.getAccessTokenExpirationMs());
    String refreshToken = jwtTokenProvider.generateToken(authToken,jwtPropertires.getRefreshTokenExpirationMs());

    //3 Authentication엔티티에 토큰 저장
    AuthenticationEntity entity = AuthenticationEntity.builder()
        .member_id(member)
        .token(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer ")
        .build();

    authenticationReposiroty.save(entity);
    return LoginResponseDTO.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer ")
        .member(MemberDTO.toDto(member))
        .build();
  }



}
