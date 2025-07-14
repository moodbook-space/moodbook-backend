package org.com.moodbook.post.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeetingJoinDto {

  private Long requestId;
  private Long memberId;
  private String memberName;
  private String status;       // PENDING또는 APPROVED 또는 REJECTED
  private String requestedAt;
}