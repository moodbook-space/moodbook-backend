package org.com.moodbook.websocket.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
  private Long roomId;
  private Long senderId;
  private String senderName;
  private String message;
  private LocalDateTime time;
}
