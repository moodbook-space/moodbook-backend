package org.com.moodbook.bookchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CreateChatRoomRequest {
  private String title;
  private int limitMembers;

  @Setter
  private MemberDTO owner;

}
