package org.com.moodbook.common.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.emotion.entity.Emotion;
import org.com.moodbook.emotion.repository.EmotionRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmotionInitializer {

  private final EmotionRepository emotionRepository;

  @PostConstruct
  // 실행과 동시에 유저가 가질 수 있는 감정 리스트를 Table에 넣어주는 역할을 수행한다
  // 감정 리스트는 서비스 실행 도중에 추가되거나 삭제되지 않기에 실행과 동시에 넣어준다.
  public void emotionInitializer() {
    // 모든 감정 리스트에 대해, DB에 추가한다.
    for (EmotionTag emotionTag : EmotionTag.values()) {
      if (!emotionRepository.existsByEmotion(emotionTag)) {
        emotionRepository.save(new Emotion(emotionTag));
        log.info("{} 감정 태그에 추가됨", emotionTag);
      }
    }
  }

}
