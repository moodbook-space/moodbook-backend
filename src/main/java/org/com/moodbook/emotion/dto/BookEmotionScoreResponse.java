package org.com.moodbook.emotion.dto;

import java.util.Map;
import lombok.Getter;
import org.com.moodbook.emotion.entity.BookEmotionScore;

@Getter
public class BookEmotionScoreResponse {
  private String isbn13;
  private String bookTitle;
  private String description;
  private Map<String, Integer> scores;
  private Long timestamp;

  public BookEmotionScoreResponse(BookEmotionScore bookEmotionScore) {
    this.isbn13 = bookEmotionScore.getIsbn13();
    this.bookTitle = bookEmotionScore.getBookTitle();
    this.description = bookEmotionScore.getDescription();
    this.scores = bookEmotionScore.getScores();
    this.timestamp = bookEmotionScore.getTimestamp();
  }

}
