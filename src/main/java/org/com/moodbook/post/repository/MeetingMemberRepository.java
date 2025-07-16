package org.com.moodbook.post.repository;

import org.com.moodbook.common.constants.MeetingJoinStatus;
import org.com.moodbook.post.entity.MeetingMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

  List<MeetingMember> findByMeetingIdAndStatus(Long meetingId, MeetingJoinStatus status);

  boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);

  // 내가 만든 모임 및 참여중인 모임 조회를 위해 필요 (마이페이지)
  Page<MeetingMember> findByMemberIdAndStatus(Long memberId, MeetingJoinStatus status, Pageable pageable);

}