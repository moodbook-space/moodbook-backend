package org.com.moodbook.book.dto;

import java.time.LocalDateTime;
import org.com.moodbook.book.entity.Book;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDTO {

  private Long id;
  private String isbn13;
  private String title;
  private String author;
  private String publisher;
  private String pubDate;
  private BigDecimal reputation;
  private String coverImage;
  private String description;
  private String categoryName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Book toEntity() {
    return Book.builder()
        .id(id)
        .isbn13(isbn13)
        .title(title)
        .author(author)
        .publisher(publisher)
        .pubDate(pubDate)
        .reputation(reputation)
        .coverImage(coverImage)
        .description(description)
        .categoryName(categoryName)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();
  }

  public static BookDTO toDTO(Book book) {
    return BookDTO.builder()
        .id(book.getId())
        .isbn13(book.getIsbn13())
        .title(book.getTitle())
        .author(book.getAuthor())
        .publisher(book.getPublisher())
        .pubDate(book.getPubDate())
        .reputation(book.getReputation())
        .coverImage(book.getCoverImage())
        .description(book.getDescription())
        .categoryName(book.getCategoryName())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }
}
