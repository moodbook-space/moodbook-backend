package org.com.moodbook.emotion.repository;

import java.util.List;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.springframework.data.domain.Pageable;

public interface BookEmotionScoreRepositoryCustom {
  List<BookEmotionScore> findByEmotionScoreDesc(String emotionTag, Pageable pageable);

}
