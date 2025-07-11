package org.com.moodbook.emotion.service;

import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.repository.BookEmotionScoreRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookEmotionService {

  private final BookEmotionScoreRepository bookEmotionScoreRepository;
  private final EmotionAnalyzer emotionAnalyzer;

  @Transactional
  public BookEmotionScoreResponse saveEmotionScore(BookEmotionScoreRequest bookEmotionRequest) throws Exception {
    String description = bookEmotionRequest.getDescription();

    Map<String, Integer> bookEmotionScoreMap = emotionAnalyzer.analyzeEmotion(description);

    BookEmotionScore emotionScore = BookEmotionScore.builder()
        .bookId(bookEmotionRequest.getBookId())
        .bookTitle(bookEmotionRequest.getBookTitle())
        .description(bookEmotionRequest.getDescription())
        .timestamp(System.currentTimeMillis())
        .build();

    emotionScore.setScores(bookEmotionScoreMap);

    // mongoDB에 저장
    bookEmotionScoreRepository.save(emotionScore);

    return new BookEmotionScoreResponse(emotionScore);
  }

}
