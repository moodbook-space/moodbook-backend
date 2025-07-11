package org.com.moodbook.post.controller;

import jakarta.validation.Valid;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;

import org.com.moodbook.post.dto.MeetingSummaryResponse;
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
@Validated
public class MeetingController {

  private final MeetingService meetingService;

  public MeetingController(MeetingService meetingService) {
    this.meetingService = meetingService;
  }

  /** 모임 생성 */
  @PostMapping
  public ResponseEntity<Long> createMeeting(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @Valid @RequestBody CreateMeetingRequest req
  ) {
    Long meetingId = meetingService.createMeeting(memberDetails.getId(), req);
    return ResponseEntity.status(201).body(meetingId);
  }

  /** 모임 단일 조회 */
  @GetMapping("/{id}")
  public ResponseEntity<MeetingDetailResponse> getMeeting(@PathVariable Long id) {
    return ResponseEntity.ok(meetingService.getMeeting(id));
  }

  /** 독서모임 목록 조회*/
  @GetMapping
  public ResponseEntity<Page<MeetingSummaryResponse>> getMeetings(Pageable pageable) {
    return ResponseEntity.ok(meetingService.getMeetings(pageable));
  }
}

