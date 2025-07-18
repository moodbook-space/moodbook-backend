package org.com.moodbook.bookchat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "ChatRoomController", description = "채팅방 관련 API")
@RequiredArgsConstructor
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final MemberRepository memberRepository;

  @Operation(summary = "채팅방 생성",
      description = "독서모임 채팅방 하나 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채팅방이 성공적으로 생성되었습니다."),
      @ApiResponse(responseCode = "500", description = "채팅방 생성에 실패했습니다.")
  })
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

  @Operation(summary = "채팅방 설정 변경",
      description = "독서모임 채팅방 설정을 수정합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채팅방이 성공적으로 수정되었습니다."),
      @ApiResponse(responseCode = "500", description = "채팅방 수정에 실패했습니다.")
  })
  @PatchMapping("/{roomId}")
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

  @Operation(summary = "모든 독서모임 채팅방 조회",
      description = "모든 독서모임 채팅방을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "모든 채팅방을 성공적으로 조회했습니다."),
      @ApiResponse(responseCode = "500", description = "모든 채팅방 조회에 실패했습니다.")
  })
  @GetMapping
  public ResponseEntity<List<ChatRoomResponse>> findAllRooms() {
    return ResponseEntity.ok(chatRoomService.findAllRooms());
  }

  @Operation(summary = "채팅방 상세 조회",
      description = "특정 독서모임 채팅방을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채팅방을 성공적으로 조회했습니다."),
      @ApiResponse(responseCode = "500", description = "채팅방 조회에 실패했습니다.")
  })
  @GetMapping("/{roomId}")
  public ResponseEntity<ChatRoomResponse> findRoomByRoomId(@PathVariable Long roomId) {
    return ResponseEntity.ok(chatRoomService.findRoomByRoomId(roomId));
  }

  @Operation(summary = "채팅방 입장 신청",
      description = "회원이 독서모임 채팅방에 신청을 요청합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공적으로 채팅방 입장 신청이 되었습니다."),
      @ApiResponse(responseCode = "500", description = "채팅방 입장 신청에 실패했습니다.")
  })
  @PostMapping("/{roomId}/join")
  public ResponseEntity<ChatRoomMemberResponse> requestJoinChatRoom(
      @PathVariable Long roomId,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    return ResponseEntity.ok(chatRoomService.requestJoinChatRoom(roomId, memberId));
  }

  @Operation(summary = "채팅방 입장 승인",
      description = "방장이 독서모임 채팅방에 가입 신청한 회원의 요청을 승인합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공적으로 승인이 되었습니다."),
      @ApiResponse(responseCode = "500", description = "승인에 실패하였습니다.")
  })
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

  @Operation(summary = "방 내 멤버/대기자 리스트 조회 (상태별 채팅 멤버 조회)",
      description = "채팅방 멤버의 가입 대기 회원 리스트를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공적으로 조회가 되었습니다."),
      @ApiResponse(responseCode = "500", description = "조회에 실패하였습니다.")
  })
  @GetMapping("/{roomId}/members")
  public ResponseEntity<List<ChatRoomMemberResponse>> getMembers(
      @PathVariable Long roomId,
      @RequestParam ChatRoomMemberStatus status) {
    return ResponseEntity.ok(chatRoomService.getMembers(roomId, status));
  }

  @Operation(summary = "채팅방 삭제",
      description = "채팅방을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공적으로 삭제가 되었습니다."),
      @ApiResponse(responseCode = "500", description = "삭제에 실패하였습니다.")
  })
  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteRoom(
      @PathVariable Long roomId,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    Long memberId = memberDetails.getId();
    chatRoomService.deleteRoom(roomId, memberId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "채팅방 나가기",
      description = "채팅방을 나갑니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "성공적으로 채팅방을 나갔습니다."),
      @ApiResponse(responseCode = "500", description = "아직 채팅방을 나가지 못했습니다.")
  })
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
