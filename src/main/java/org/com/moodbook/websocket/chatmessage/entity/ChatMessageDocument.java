package org.com.moodbook.websocket.chatmessage.entity;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "chat_message")
public class ChatMessageDocument {

  @Id
  private String id;

  private Long roomId;
  private Long senderId;
  private String senderName;
  private String message;
  private LocalDateTime time;
}