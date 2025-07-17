package org.com.moodbook.bookchat.dto;

import lombok.Getter;

@Getter
public class MemberDTO {
  private Long memberId;
  private String memberName;

  public MemberDTO(Long memberId, String memberName) {
    this.memberId = memberId;

  }
}
