package org.com.moodbook.bookchat.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.bookchat.entity.ChatRoom;
import org.com.moodbook.bookchat.entity.ChatRoomMember;

@Getter
@Builder
public class ChatRoomMemberResponse {
  private Long chatRoomId;
  private Long memberId;
  private String memberName;
  private String status;
  private String role;

  public static ChatRoomMemberResponse from(ChatRoomMember chatRoomMember) {
    return ChatRoomMemberResponse.builder()
        .chatRoomId(chatRoomMember.getChatRoom().getId())
        .memberId(chatRoomMember.getMember().getId())
        .memberName(chatRoomMember.getMember().getName())
        .status(chatRoomMember.getStatus().name())
        .role(chatRoomMember.getRole().name())
        .build();
  }

}
