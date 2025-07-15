package org.com.moodbook.emotion.repository;

import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.emotion.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
  Boolean existsByEmotion(EmotionTag emotion);

  Long getIdByEmotion(EmotionTag emotion);
}
