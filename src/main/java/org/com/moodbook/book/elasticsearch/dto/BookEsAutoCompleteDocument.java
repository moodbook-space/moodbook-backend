package org.com.moodbook.book.elasticsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "book-index-v2")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookEsAutoCompleteDocument {

  @Id
  private Long bookId;

  private String isbn13;
  private String title;
  private String author;
  private BigDecimal reputation;
  private String coverImage;

  public static BookEsAutoCompleteDocument fromEntity(Book dto) {
    return BookEsAutoCompleteDocument.builder()
        .bookId(dto.getId())
        .isbn13(dto.getIsbn13())
        .title(dto.getTitle())
        .author(dto.getAuthor())
        .reputation(dto.getReputation())
        .coverImage(dto.getCoverImage())
        .build();
  }
}
