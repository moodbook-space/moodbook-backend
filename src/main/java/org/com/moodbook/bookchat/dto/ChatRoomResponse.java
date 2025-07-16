package org.com.moodbook.bookchat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.bookchat.entity.ChatRoom;

@Getter
@Builder
public class ChatRoomResponse {
  private Long chatRoomId;
  private String title;
  private int limitMember;
  private String status;
  private Long ownerId;
  private String ownerName;
  private LocalDateTime createdAt;

  public ChatRoomResponse(Long chatRoomId, String title, int limitMember, String status, Long ownerId, String ownerName, LocalDateTime createdAt) {
    this.chatRoomId = chatRoomId;
    this.title = title;
    this.limitMember = limitMember;
    this.status = status;
    this.ownerId = ownerId;
    this.ownerName = ownerName;
    this.createdAt = createdAt;
  }

  public static ChatRoomResponse fromEntity(ChatRoom room) {
    return ChatRoomResponse.builder()
        .chatRoomId(room.getId())
        .title(room.getTitle())
        .limitMember(room.getLimitMembers())
        .status(room.getStatus().name())
        .ownerId(room.getOwner().getId())
        .ownerName(room.getOwner().getName())
        .createdAt(room.getCreatedAt())
        .build();
  }

}
