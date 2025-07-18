package org.com.moodbook.bookchat.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ApproveJoinRequest {

  private Long approveId;
  private Long chatRoomMemberId;
  private boolean approve;

}
