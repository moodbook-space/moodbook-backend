package org.com.moodbook.post.service;

import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingService {

  // 새로운 독서모임 생성
  Long createMeeting(Long memberId, CreateMeetingRequest request);

  // 단일 독서모임 조회
  MeetingDetailResponse getMeeting(Long meetingId);

  // 미팅 목록 조회
  Page<MeetingSummaryResponse> getMeetings(Pageable pageable);
}
