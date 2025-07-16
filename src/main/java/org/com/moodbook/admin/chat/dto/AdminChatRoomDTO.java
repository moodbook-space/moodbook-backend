package org.com.moodbook.admin.chat.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.com.moodbook.bookchat.entity.ChatRoomStatus;

@Data
@AllArgsConstructor
public class AdminChatRoomDTO {

  private Long id;
  private String title;
  private int limitMembers;
  private String nickname;
  private ChatRoomStatus status;
  private LocalDateTime createdAt;
  private long participants;

}
