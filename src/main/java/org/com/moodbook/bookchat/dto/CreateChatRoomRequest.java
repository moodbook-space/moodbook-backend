package org.com.moodbook.bookchat.dto;

import lombok.Getter;

@Getter
public class CreateChatRoomRequest {
  private String title;
  private int limitMembers;
  private MemberDTO owner;

}
