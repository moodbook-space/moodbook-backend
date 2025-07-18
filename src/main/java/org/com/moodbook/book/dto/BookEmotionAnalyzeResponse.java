package org.com.moodbook.book.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.book.entity.Book;

@Builder
@Getter
public class BookEmotionAnalyzeResponse {
  Long bookId;
  String isbn13;
  String title;
  String description;

  public static BookEmotionAnalyzeResponse from(Book book) {
    return BookEmotionAnalyzeResponse.builder()
        .bookId(book.getId())
        .isbn13(book.getIsbn13())
        .title(book.getTitle())
        .description(book.getDescription())
        .build();
  }

}
