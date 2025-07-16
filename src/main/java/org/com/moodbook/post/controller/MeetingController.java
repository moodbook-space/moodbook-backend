package org.com.moodbook.post.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.util.PageableUtil;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;

import org.com.moodbook.post.dto.MeetingJoinDto;
import org.com.moodbook.post.dto.MeetingJoinResponseRequest;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.com.moodbook.post.service.MeetingService;


import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
   * 독서모임 단일 조회
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
   * 모임 참가 신청
   */
  @PostMapping("/{id}/requests")
  public ResponseEntity<Void> requestJoin(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long meetingId
  ) {
    meetingService.requestJoinMeeting(md.getId(), meetingId);
    return ResponseEntity.status(201).build();
  }

  /**
   * 모임장 전용: 대기 중인 신청자 목록 조회
   */
  @GetMapping("/{id}/requests")
  public ResponseEntity<List<MeetingJoinDto>> listJoinRequests(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long meetingId
  ) {
    List<MeetingJoinDto> list = meetingService.listJoinRequests(md.getId(), meetingId);
    return ResponseEntity.ok(list);
  }

  /**
   * 모임장 승인/거절
   */
  @PatchMapping("/{id}/requests/{reqId}")
  public ResponseEntity<Void> respondRequest(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long meetingId,
      @PathVariable("reqId") Long requestId,
      @Valid @RequestBody MeetingJoinResponseRequest body
  ) {
    meetingService.respondToJoinRequest(md.getId(), meetingId, requestId, body);
    return ResponseEntity.noContent().build();
  }

  /**
   * 내가 만든 모임 및 참여중인 모임목록을 조회할수있는 엔드포인트(마이페이지)
   */
  @GetMapping("/api/meetings/my")
  public ResponseEntity<Page<MeetingSummaryResponse>> getMyMeetings(
      @AuthenticationPrincipal CustomMemberDetails md,
      @RequestParam(defaultValue = "host") String role,
      Pageable pageable
  ) {
    return ResponseEntity.ok(meetingService.getMyMeetings(md.getId(), role, pageable));
  }
}

