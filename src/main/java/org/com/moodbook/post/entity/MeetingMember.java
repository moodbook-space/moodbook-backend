package org.com.moodbook.post.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.common.constants.MeetingJoinStatus;
import org.com.moodbook.common.model.BaseTime;



import java.time.LocalDateTime;
import org.com.moodbook.member.entity.Member;

/**
 * 독서모임 참가 신청/멤버 관리 엔티티
 */
@Entity
@Table(
    name = "meeting_member",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_meeting_member",
        columnNames = { "meeting_id", "member_id" }
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MeetingMember extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 참가 신청한 모임 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_id", nullable = false)
  private Meeting meeting;

  /** 신청한 회원 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  /** 신청 상태 (PENDING: 대기, APPROVED: 승인, REJECTED: 거절) */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeetingJoinStatus status;

  /** 승인 시 참여 확정 일시 (status == APPROVED 인 경우 설정) */
  private LocalDateTime joinedAt;
}