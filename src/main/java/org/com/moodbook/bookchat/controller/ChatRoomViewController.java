package org.com.moodbook.bookchat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.dto.ChatRoomResponse;
import org.com.moodbook.bookchat.dto.ChatRoomMemberResponse;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.service.ChatRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ChatRoomViewController {

  private final ChatRoomService chatRoomService;

  @GetMapping("/chat-rooms")
  public String chatRoomListPage(Model model) {
    List<ChatRoomResponse> chatRooms = chatRoomService.findAllRooms();
    model.addAttribute("chatRooms", chatRooms);
    return "chatroom/chat-rooms";
  }

  @GetMapping("/chat-rooms/{roomId}")
  public String chatRoomDetailPage(
      @PathVariable Long roomId,
      @SessionAttribute(name = "memberId", required = false) Long myMemberId,
      Model model) {

    ChatRoomResponse chatRoom = chatRoomService.findRoomByRoomId(roomId);

    List<ChatRoomMemberResponse> members = chatRoomService.getMembers(roomId, null);

    boolean isMember = members.stream().anyMatch(m -> m.getMemberId().equals(myMemberId) && m.getStatus().equals("APPROVED"));

    boolean isOwner = chatRoom.getOwnerId().equals(myMemberId);

    model.addAttribute("chatRoom", chatRoom);
    model.addAttribute("members", members);
    model.addAttribute("myMemberId", myMemberId);
    model.addAttribute("isMember", isMember);
    model.addAttribute("isOwner", isOwner);

    return "chatroom/chat-room-detail";
  }

  // 채팅방 내부(채팅) 화면
  @GetMapping("/chat-rooms/{roomId}/chat")
  public String chatRoomChatPage(@PathVariable Long roomId, Model model) {
    ChatRoomResponse room = chatRoomService.findRoomByRoomId(roomId);
    model.addAttribute("room", room);

    return "chatroom/chat-room-chat";
  }


  @GetMapping("/chat-rooms/create")
  public String createChatRoomForm() {
    return "chatroom/create-room";
  }

  @GetMapping("/chat-rooms/{roomId}/waiting-members")
  public String waitingMembersPage(@PathVariable Long roomId, Model model) {

    ChatRoomResponse chatRoom = chatRoomService.findRoomByRoomId(roomId);

    List<ChatRoomMemberResponse> waitingMembers =
        chatRoomService.getMembers(roomId, ChatRoomMemberStatus.WAITING);

    // 3. 모델에 담아서 뷰로 전달
    model.addAttribute("chatRoom", chatRoom);
    model.addAttribute("waitingMembers", waitingMembers);
    return "chatroom/waiting-members";
  }



}
