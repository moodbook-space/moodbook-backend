package org.com.moodbook.emotion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.member.entity.Member;

// 각 유저가 가진 감정을 기록하기 위함
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member_emotion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "emotion_Id"})})
public class MemberEmotion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emotion_id", nullable = false)
  private Emotion emotion;

  // 정적 팩토리 메소드, memberId와 emotionId로 MemberEmotion을 만든다.
  public static MemberEmotion of(Long memberId, Long emotionId) {
    return MemberEmotion.builder().member(Member.builder().id(memberId).build())
        .emotion(Emotion.builder().id(emotionId).build()).build();
  }
}