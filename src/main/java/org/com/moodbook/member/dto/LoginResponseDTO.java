package org.com.moodbook.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

  private String accessToken;
  private String refreshToken;
  private String tokenType;//보통 Bearer
  private MemberDTO member;// 로그인된 사용자 정보



}
