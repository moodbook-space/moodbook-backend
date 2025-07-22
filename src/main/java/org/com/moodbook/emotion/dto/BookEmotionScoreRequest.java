package org.com.moodbook.emotion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookEmotionScoreRequest {
  private Long bookId;
  private String isbn13;
  private String bookTitle;
  private String description;

}
