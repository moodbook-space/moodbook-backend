package org.com.moodbook.bookchat.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.dto.*;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.service.ChatRoomService;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {

    Long memberId = memberDetails.getId();
    String memberName = memberDetails.getUsername();

    if (request.getOwner() == null) {

      request.setOwner(new MemberDTO(memberId, memberName));
    }
    return ResponseEntity.ok(chatRoomService.createRoom(request));
  }


  @PutMapping("/{roomId}")
  public ResponseEntity<ChatRoomResponse> updateRoomSettings(
      @PathVariable Long roomId,
      @RequestBody UpdateChatRoomRequest request,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
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
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    return ResponseEntity.ok(chatRoomService.requestJoinChatRoom(roomId, memberId));
  }

  @PostMapping("/members/approve")
  public ResponseEntity<ChatRoomMemberResponse> approveJoinChatRoom(
      @RequestBody ApproveJoinRequest request,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    request.setApproveId(memberId); // 세션에서 받아서 approveId에 주입
    ChatRoomMemberResponse response = chatRoomService.approveJoinChatRoom(request);
    return ResponseEntity.ok(response);
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
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    chatRoomService.deleteRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{roomId}/leave")
  public ResponseEntity<Void> leaveRoom(
      @PathVariable Long roomId,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    chatRoomService.leaveRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

}
