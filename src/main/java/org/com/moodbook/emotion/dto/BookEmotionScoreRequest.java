package org.com.moodbook.emotion.dto;

import lombok.Getter;

@Getter
public class BookEmotionScoreRequest {
  private Long bookId;
  private String isbn13;
  private String bookTitle;
  private String description;

}
