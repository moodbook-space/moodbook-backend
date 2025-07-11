package org.com.moodbook.post.repository;

import org.com.moodbook.common.constants.MeetingJoinStatus;
import org.com.moodbook.post.entity.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

  List<MeetingMember> findByMeetingIdAndStatus(Long meetingId, MeetingJoinStatus status);

  boolean existsByMeetingIdAndMemberId(Long meetingId, Long memberId);
}