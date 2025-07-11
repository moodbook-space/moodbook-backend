package org.com.moodbook.book.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import org.com.moodbook.book.entity.Book;

public class BookResponse {
  private Long bookId;
  private String title;
  private String author;
  private String publisher;
  private Date pubDate;
  private BigDecimal reputation;
  private String cover_image;
  private String description;
  private String category_name;

  @Builder
  public BookResponse(Book book) {
    this.bookId = book.getId();
    this.title = book.getTitle();
    this.author = book.getAuthor();
    this.publisher = book.getPublisher();
    this.pubDate = book.getPubDate();
    this.reputation = book.getReputation();
    this.cover_image = book.getCoverImage();
    this.description = book.getDescription();
    this.category_name = book.getCategoryName();
  }

  public static BookResponse fromEntity(Book book) {
    return BookResponse.builder()
        .book(book)   // 생성자에 Book 넣어서 변환
        .build();
  }



}
