package org.com.moodbook.member.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.member.entity.Member;

@Getter
@Builder
public class LoginResponseDTO {

  private String accessToken;
  private String refreshToken;
  private Long member;

}
