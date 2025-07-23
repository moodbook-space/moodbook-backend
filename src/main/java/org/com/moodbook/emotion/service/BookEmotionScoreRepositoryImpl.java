package org.com.moodbook.emotion.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.repository.BookEmotionScoreRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookEmotionScoreRepositoryImpl implements BookEmotionScoreRepositoryCustom {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<BookEmotionScore> findByEmotionScoreDesc(String emotionTag, Pageable pageable) {
    Aggregation agg = Aggregation.newAggregation(
        Aggregation.project("isbn13", "bookId", "bookTitle", "scores")
            .and("scores." + emotionTag).as("emotionScore"),
        Aggregation.sort(Sort.Direction.DESC, "emotionScore"),
        Aggregation.skip(pageable.getOffset()),
        Aggregation.limit(pageable.getPageSize())
    );
    return mongoTemplate.aggregate(agg, "emotion_scores", BookEmotionScore.class).getMappedResults();
  }

}
