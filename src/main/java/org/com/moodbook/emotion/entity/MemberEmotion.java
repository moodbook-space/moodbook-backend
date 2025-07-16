package org.com.moodbook.emotion.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
public class MemberEmotion {

  @EmbeddedId
  private MemberEmotionId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("memberId")
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("emotionId")
  @JoinColumn(name = "emotion_id")
  private Emotion emotion;

  // 정적 팩토리 메소드
  public static MemberEmotion of(Long memberId, Long emotionId) {
    return MemberEmotion.builder()
        .id(new MemberEmotionId(memberId, emotionId))
        .member(Member.builder().id(memberId).build())
        .emotion(Emotion.builder().id(emotionId).build())
        .build();
  }
}