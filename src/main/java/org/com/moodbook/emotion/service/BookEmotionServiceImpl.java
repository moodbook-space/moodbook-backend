package org.com.moodbook.emotion.service;

import jakarta.transaction.Transactional;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.repository.BookEmotionScoreRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookEmotionServiceImpl implements BookEmotionService {


  private final BookEmotionScoreRepository bookEmotionScoreRepository;
  private final EmotionAnalyzer emotionAnalyzer;

  public static Map<EmotionTag, Integer> toEnumMap(Map<String, Integer> stringMap) {
    Map<EmotionTag, Integer> result = new EnumMap<>(EmotionTag.class);
    for (Map.Entry<String, Integer> entry : stringMap.entrySet()) {
      EmotionTag tag = switch (entry.getKey()) {
        case "기쁨" -> EmotionTag.JOY;
        case "슬픔" -> EmotionTag.SADNESS;
        case "분노" -> EmotionTag.ANGER;
        case "불안" -> EmotionTag.ANXIETY;
        case "설렘" -> EmotionTag.EXCITEMENT;
        case "위로" -> EmotionTag.COMFORT;
        case "외로움" -> EmotionTag.LONELINESS;
        case "감동" -> EmotionTag.INSPIRATION;
        case "행복" -> EmotionTag.HAPPINESS;
        default -> throw BaseException.EMOTION_NOT_FOUND;
      };
      result.put(tag, entry.getValue());
    }
    return result;
  }


  @Transactional
  public BookEmotionScoreResponse saveEmotionScore(BookEmotionScoreRequest bookEmotionRequest)
      throws Exception {
    String description = bookEmotionRequest.getDescription();

    Map<String, Integer> bookEmotionScoreMap = emotionAnalyzer.analyzeEmotion(description);

    Map<EmotionTag, Integer> emotionScoreEnumMap = toEnumMap(bookEmotionScoreMap);


    BookEmotionScore emotionScore = BookEmotionScore.builder()
        .bookId(bookEmotionRequest.getBookId())
        .bookTitle(bookEmotionRequest.getBookTitle())
        .description(bookEmotionRequest.getDescription())
        .timestamp(System.currentTimeMillis())
        .build();

    emotionScore.setScores(emotionScoreEnumMap);

    // mongoDB에 저장
    bookEmotionScoreRepository.save(emotionScore);

    return new BookEmotionScoreResponse(emotionScore);
  }

}
