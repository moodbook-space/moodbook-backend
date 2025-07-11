package org.com.moodbook.emotion.repository;

import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookEmotionScoreRepository extends MongoRepository<BookEmotionScore, Long> {

}
