package org.com.moodbook.bookchat.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.dto.*;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.service.ChatRoomService;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final MemberRepository memberRepository;

  @PostMapping
  public ResponseEntity<ChatRoomResponse> createRoom(
      @RequestBody CreateChatRoomRequest request,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    // 세션에서 owner로 지정
    if (request.getOwner() == null && memberId != null) {
      Member member = memberRepository.findById(memberId).orElse(null);
      assert member != null;
      request.setOwner(new MemberDTO(memberId, member.getName()));
    }
    return ResponseEntity.ok(chatRoomService.createRoom(request));
  }


  @PutMapping("/{roomId}")
  public ResponseEntity<ChatRoomResponse> updateRoomSettings(
      @PathVariable Long roomId,
      @RequestBody UpdateChatRoomRequest request,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    request.setChatRoomId(roomId);
    request.setMemberId(memberId);
    return ResponseEntity.ok(chatRoomService.updateRoomSettings(request));
  }

  @GetMapping
  public ResponseEntity<List<ChatRoomResponse>> findAllRooms() {
    return ResponseEntity.ok(chatRoomService.findAllRooms());
  }

  @GetMapping("/{roomId}")
  public ResponseEntity<ChatRoomResponse> findRoomByRoomId(@PathVariable Long roomId) {
    return ResponseEntity.ok(chatRoomService.findRoomByRoomId(roomId));
  }

  @PostMapping("/{roomId}/join")
  public ResponseEntity<ChatRoomMemberResponse> requestJoinChatRoom(
      @PathVariable Long roomId,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    return ResponseEntity.ok(chatRoomService.requestJoinChatRoom(roomId, memberId));
  }

  @PostMapping("/members/approve")
  public ResponseEntity<Void> approveJoinChatRoom(
      @RequestBody ApproveJoinRequest request,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    request.setApproveId(memberId); // 세션에서 받아서 approveId에 주입
    chatRoomService.approveJoinChatRoom(request);
    return ResponseEntity.ok().build();
  }


  @GetMapping("/{roomId}/members")
  public ResponseEntity<List<ChatRoomMemberResponse>> getMembers(
      @PathVariable Long roomId,
      @RequestParam ChatRoomMemberStatus status) {
    return ResponseEntity.ok(chatRoomService.getMembers(roomId, status));
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteRoom(
      @PathVariable Long roomId,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    chatRoomService.deleteRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{roomId}/leave")
  public ResponseEntity<Void> leaveRoom(
      @PathVariable Long roomId,
      @SessionAttribute(name = "memberId", required = false) Long memberId
  ) {
    chatRoomService.leaveRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

}
