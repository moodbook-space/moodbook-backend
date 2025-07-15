package org.com.moodbook.bookchat.dto;

import lombok.Getter;

@Getter
public class ApproveJoinRequest {

  private Long approveId;
  private Long chatRoomMemberId;
  private boolean approve;

}
