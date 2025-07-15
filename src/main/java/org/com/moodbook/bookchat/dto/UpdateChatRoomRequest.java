package org.com.moodbook.bookchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateChatRoomRequest {
  private String role;
  private String title;
  private int limitMembers;
  private MemberDTO owner;
  private Long chatRoomId;

}
