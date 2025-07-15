package org.com.moodbook.bookchat.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.dto.*;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.service.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  @PostMapping
  public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody CreateChatRoomRequest request) {
    return ResponseEntity.ok(chatRoomService.createRoom(request));
  }

  @PutMapping("/{roomId}")
  public ResponseEntity<ChatRoomResponse> updateRoomSettings(
      @PathVariable Long roomId,
      @RequestBody UpdateChatRoomRequest request) {

    request.setChatRoomId(roomId);
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
      @RequestParam Long memberId) {
    return ResponseEntity.ok(chatRoomService.requestJoinChatRoom(roomId, memberId));
  }

  @PostMapping("/members/approve")
  public ResponseEntity<Void> approveJoinChatRoom(@RequestBody ApproveJoinRequest request) {
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
      @RequestParam Long memberId) {
    chatRoomService.deleteRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{roomId}/leave")
  public ResponseEntity<Void> leaveRoom(
      @PathVariable Long roomId,
      @RequestParam Long memberId) {
    chatRoomService.leaveRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }
}
