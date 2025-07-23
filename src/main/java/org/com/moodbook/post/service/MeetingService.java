package org.com.moodbook.post.service;

import org.com.moodbook.post.dto.ChatLinkRequest;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeetingService {
  // 모임 글 생성
  Long createMeeting(Long memberId, CreateMeetingRequest request);

  // 모임 글 상세 조회
  MeetingDetailResponse getMeeting(Long memberId, Long meetingId);

  // 모임 글 목록 조회
  Page<MeetingSummaryResponse> getMeetings(Long memberId, Pageable pageable);

  // 모임 글 수정
  void updateMeeting(Long memberId, Long meetingId, UpdateMeetingRequest request);

  // 모임 삭제
  void deleteMeeting(Long memberId, Long meetingId);

  // 내가 만든 모임글 목록 조회
  Page<MeetingSummaryResponse> getMyMeetings(Long memberId, Pageable pageable);

  // 채팅방 링크 메서드
  void linkChatRoom(Long hostId, Long meetingId, ChatLinkRequest req);
}