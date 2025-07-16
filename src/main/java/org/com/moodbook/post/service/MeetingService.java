package org.com.moodbook.post.service;

import java.util.List;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingJoinDto;
import org.com.moodbook.post.dto.MeetingJoinResponseRequest;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingService {

  Long createMeeting(Long memberId, CreateMeetingRequest request);

  MeetingDetailResponse getMeeting(Long meetingId);

  Page<MeetingSummaryResponse> getMeetings(Pageable pageable);

  // 모임 수정
  void updateMeeting(Long memberId, Long meetingId, UpdateMeetingRequest request);

  // 모임 삭제
  void deleteMeeting(Long memberId, Long meetingId);

  // 모임 참가 신청
  void requestJoinMeeting(Long memberId, Long meetingId);

  // 모임장 승인/거절
  void respondToJoinRequest(Long hostId, Long meetingId, Long requestId, MeetingJoinResponseRequest req);

  // 모임장을 위한 메서드 - 대기 중인 신청 목록 조회
  List<MeetingJoinDto> listJoinRequests(Long hostId, Long meetingId);

  /**
   * 내가 만든(호스트) 또는 참가 중인 모임 목록 조회
   * @param role "host" 또는 "participant"
   */
  Page<MeetingSummaryResponse> getMyMeetings(Long memberId, String role, Pageable pageable);
}