package org.com.moodbook.emotion.service;

import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;

public interface BookEmotionService {

  public BookEmotionScoreResponse saveEmotionScore(BookEmotionScoreRequest bookEmotionRequest)
      throws Exception;


}
