package org.com.moodbook.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "MeetingController", description = "독서 모임에 대한 컨트롤러")
@Validated
public class MeetingController {

  private final MeetingService meetingService;

  /**
   * 모임 생성
   */
  @PostMapping
  @Operation(summary = "독서 모임 생성",
      description = "독서 모임을 추가합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독서 모임이 추가되었습니다."),
      @ApiResponse(responseCode = "500", description = "독서 모임 추가에 실패하였습니다.")
  })
  public ResponseEntity<Long> createMeeting(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @Valid @RequestBody CreateMeetingRequest req
  ) {
    Long meetingId = meetingService.createMeeting(memberDetails.getId(), req);
    return ResponseEntity.status(201).body(meetingId);
  }

  /**
   * 독서모임 상세 조회
   */
  @GetMapping("/{id}")
  @Operation(summary = "독서 모임 상세 조회",
      description = "독서 모임을 상세 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독서 모임을 성공적으로 상세히 조회하였습니다."),
      @ApiResponse(responseCode = "500", description = "독서 모임 조회에 실패하였습니다(상세 조회).")
  })
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
  @Operation(summary = "독서 모임 목록 조회",
      description = "독서 모임을 목록을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독서 모임을 성공적으로 조회하였습니다."),
      @ApiResponse(responseCode = "500", description = "독서 모임 조회에 실패하였습니다.")
  })
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
  @Operation(summary = "독서 모임 수정",
      description = "독서 모임 설정 내용을 수정합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독서 모임을 성공적으로 수정하였습니다."),
      @ApiResponse(responseCode = "500", description = "독서 모임 수정에 실패하였습니다.")
  })
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
  @Operation(summary = "독서 모임 삭제",
      description = "독서 모임을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독서 모임을 성공적으로 삭제하였습니다."),
      @ApiResponse(responseCode = "500", description = "독서 모임 삭제에 실패하였습니다.")
  })
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
  @Operation(summary = "마이페이지에서 독서 모임 목록 조회",
      description = "마이페이지에서 독서 모임 목록을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "마이페이지에서 독서 모임을 성공적으로 조회하였습니다."),
      @ApiResponse(responseCode = "500", description = "마이페이지에서 독서 모임을 삭제하는데 실패하였습니다.")
  })
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
  @Operation(summary = "채팅방 링크 연결",
      description = "채팅방 링크를 연결합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "채팅방 링크 연결에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "채팅방 링크 연결에 실패하였습니다.")
  })
  public ResponseEntity<Void> linkChatRoom(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long meetingId,
      @Valid @RequestBody ChatLinkRequest req
  ) {
    meetingService.linkChatRoom(md.getId(), meetingId, req);
    return ResponseEntity.noContent().build();
  }
}

