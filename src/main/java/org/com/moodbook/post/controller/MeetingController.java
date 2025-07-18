package org.com.moodbook.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.util.PageableUtil;
import org.com.moodbook.post.dto.ChatLinkRequest;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.com.moodbook.post.service.MeetingService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Validated
public class MeetingController {

  private final MeetingService meetingService;

  /**
   * 모임 생성
   */
  @PostMapping
  public ResponseEntity<Long> createMeeting(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @Valid @RequestBody CreateMeetingRequest req
  ) {
    Long meetingId = meetingService.createMeeting(memberDetails.getId(), req);
    return ResponseEntity.status(201).body(meetingId);
  }

  /**
   * 독서모임 상세 조회
   * GET /api/meetings/{meetingId}
   * Response JSON (MeetingDetailResponse)에 아래 필드 포함
   *  - chatRoomId  ← 생성된 채팅방 ID (null 가능)
   * 프론트에서
   * 1. 이 API 호출 후 반환된 chatRoomId가 있을 때만
   *    “채팅방 입장하기” 버튼 노출
   * 2. 버튼 클릭 시 /chat-rooms/{chatRoomId}로 이동
   */
  @GetMapping("/{id}")
  public ResponseEntity<MeetingDetailResponse> getMeeting(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long id
  ) {
    MeetingDetailResponse detail = meetingService.getMeeting(md.getId(), id);
    return ResponseEntity.ok(detail);
  }

  /**
   * 독서모임 목록 조회 (정렬은 좋아요순 조회수순 최신순 가능)
   */
  @GetMapping
  public ResponseEntity<Page<MeetingSummaryResponse>> getMeetings(
      @AuthenticationPrincipal CustomMemberDetails md,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "latest") String sortType
  ) {
    Pageable pageable = PageableUtil.of(page, size, sortType);
    Page<MeetingSummaryResponse> result = meetingService.getMeetings(md.getId(), pageable);
    return ResponseEntity.ok(result);
  }

  /**
   * 모임 수정
   */
  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateMeeting(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long id,
      @Valid @RequestBody UpdateMeetingRequest req
  ) {
    meetingService.updateMeeting(md.getId(), id, req);
    return ResponseEntity.noContent().build();
  }

  /**
   * 모임 삭제
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMeeting(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long id
  ) {
    meetingService.deleteMeeting(md.getId(), id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 내가 만든 모임 및 참여중인 모임목록을 조회할수있는 엔드포인트(마이페이지)
   */
  @GetMapping("/my")
  public ResponseEntity<Page<MeetingSummaryResponse>> getMyMeetings(
      @AuthenticationPrincipal CustomMemberDetails md,
      Pageable pageable
  ) {
    return ResponseEntity.ok(
        meetingService.getMyMeetings(md.getId(), pageable)
    );
  }

  /**
   * 채팅방 링크 연결
   */
  @PostMapping("/{id}/chat-link")
  public ResponseEntity<Void> linkChatRoom(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long meetingId,
      @Valid @RequestBody ChatLinkRequest req
  ) {
    meetingService.linkChatRoom(md.getId(), meetingId, req);
    return ResponseEntity.noContent().build();
  }
}

