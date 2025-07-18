package org.com.moodbook.bookchat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ApproveJoinRequest {
  private Long roomId;

  @Setter
  private Long approveId;
  private Long chatRoomMemberId;
  private boolean approve;

}
