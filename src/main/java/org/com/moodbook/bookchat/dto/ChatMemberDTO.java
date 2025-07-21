package org.com.moodbook.bookchat.dto;

import lombok.Getter;

@Getter
public class ChatMemberDTO {
  private Long memberId;
  private String memberName;

  public ChatMemberDTO(Long memberId, String memberName) {
    this.memberId = memberId;
    this.memberName = memberName;
  }
}
