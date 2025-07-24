package org.com.moodbook.websocket.chatmessage.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.websocket.chatmessage.entity.ChatMessageDocument;
import org.com.moodbook.websocket.dto.ChatMessage;


@Getter
@Builder
public class ChatMessageDocumentResponse {
  private Long roomId;
  private String senderName;
  private Long senderId;
  private String message;
  private LocalDateTime time;

  // ChatMessage로부터 변환
  public static ChatMessageDocumentResponse from(ChatMessage chatMessage) {
    return ChatMessageDocumentResponse.builder()
        .roomId(chatMessage.getRoomId())
        .senderName(chatMessage.getSenderName())
        .senderId(chatMessage.getSenderId())
        .message(chatMessage.getMessage())
        .time(chatMessage.getTime()) // LocalDateTime이 아닐 경우 적절히 변환
        .build();
  }

  // ChatMessageDocument로부터 변환
  public static ChatMessageDocumentResponse from(ChatMessageDocument doc) {
    return ChatMessageDocumentResponse.builder()
        .roomId(doc.getRoomId())
        .senderName(doc.getSenderName())
        .senderId(doc.getSenderId())
        .message(doc.getMessage())
        .time(doc.getTime())
        .build();
  }
}
