package org.com.moodbook.book.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.book.entity.Book;

@Getter
@Builder
public class BookEmotionRecommendAllResponse {
  Long bookId;
  String isbn13;
  String title;
  String coverImage;
  BigDecimal reputation;

  public static BookEmotionRecommendAllResponse from(Book book) {
    return BookEmotionRecommendAllResponse.builder()
        .bookId(book.getId())
        .isbn13(book.getIsbn13())
        .title(book.getTitle())
        .coverImage(book.getCoverImage())
        .reputation(book.getReputation())
        .build();
  }

}
