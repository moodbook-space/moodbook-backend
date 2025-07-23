package org.com.moodbook.security.authentication.service;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.security.authentication.entity.AuthenticationEntity;
import org.com.moodbook.security.authentication.repository.AuthenticationRepository;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.com.moodbook.security.jwt.JwtProperties;
import org.com.moodbook.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final AuthenticationRepository authenticationRepository;
  private final MemberRepository memberRepository;


  @Override
  public LoginResponseDTO issueTokenAndStore(MemberDTO memberDTO) {
    Member member = memberDTO.toEntity();
    //AuthenticationEntity 객체 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        new CustomMemberDetails(member),
        null,
        Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
    //2. Jwt토큰 생성
    String accessToken = jwtTokenProvider.generateToken(authToken,
        jwtProperties.getAccessTokenExpirationMs(), "access");
    String refreshToken = jwtTokenProvider.generateToken(authToken,
        jwtProperties.getRefreshTokenExpirationMs(), "refresh");

    //3 Authentication엔티티에 토큰 저장
    AuthenticationEntity entity = AuthenticationEntity.builder()
        .member(member)
        .refreshToken(refreshToken)
        .build();

    authenticationRepository.save(entity);
    return LoginResponseDTO.builder()
        .member(member.getId())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public String reIssueAccessToken(String refreshToken) {
    // 1. Bearer 제거
    String pureToken = refreshToken.replace("Bearer ", "");

    // 2. 유효성 검사
    if (!jwtTokenProvider.validateToken(pureToken)) {
      throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 3. 사용자 식별
    Long memberId = jwtTokenProvider.getUserIdFromToken(pureToken);

    // 4. DB에서 저장 된 토큰 비교
    AuthenticationEntity authentication = authenticationRepository.findByMember_Id(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (!authentication.getRefreshToken().equals(pureToken)) {
      throw new BaseException(ErrorCode.REFRESH_TOKEN_MISMATCH);
    }
    Member member = authentication.getMember();
      return jwtTokenProvider.generateToken(
        new UsernamePasswordAuthenticationToken(
          new CustomMemberDetails(member),
            null,
            Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()))
        ),
        jwtProperties.getAccessTokenExpirationMs(),
        "access"
    );

  }




}
