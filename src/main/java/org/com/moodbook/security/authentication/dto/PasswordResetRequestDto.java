package org.com.moodbook.security.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequestDto {

  private String token;
  private String newPassword;

}
