package org.com.moodbook.admin.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminChatRoomDTO {

  private Long id;
  private Long participants;
  private String name;
  private String createdBy;
  private String description;
  private LocalDateTime createdAt;

  //camel case 로 변경 얘정

}
