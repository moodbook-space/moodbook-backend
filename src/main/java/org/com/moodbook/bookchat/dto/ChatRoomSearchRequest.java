package org.com.moodbook.bookchat.dto;

import lombok.Getter;

@Getter
public class ChatRoomSearchRequest {

  private String title;
  private String ownerName;
  private Integer minLimitMembers;
  private Integer maxLimitMembers;

}
