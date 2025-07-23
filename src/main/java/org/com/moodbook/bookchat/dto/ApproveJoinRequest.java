package org.com.moodbook.bookchat.dto;

import lombok.Data;

@Data
public class ApproveJoinRequest {
  private Long roomId;

  private Long approveId;
  private Long chatRoomMemberId;
  private boolean approve;

}