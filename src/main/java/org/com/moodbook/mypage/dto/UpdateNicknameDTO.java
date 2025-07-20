package org.com.moodbook.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateNicknameDTO {

  private String nickname;

  // Builder를 사용한 Static Factory Method
  public static UpdateNicknameDTO of(String nickname) {
    return UpdateNicknameDTO.builder()
        .nickname(nickname)
        .build();
  }
}
