package org.com.moodbook.emotion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.EmotionTag;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 사용자가 가질 수 있는 감정 리스트를 의미한다.
public class Emotion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  @Enumerated(EnumType.STRING)
  private EmotionTag emotion;

  // 감정 태그 기반으로 값을 생성하는 생성자
  public Emotion(EmotionTag emotion) {
    this.emotion = emotion;
  }
}
