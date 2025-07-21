package org.com.moodbook.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

  private String accessToken;
  private String refreshToken;

}
