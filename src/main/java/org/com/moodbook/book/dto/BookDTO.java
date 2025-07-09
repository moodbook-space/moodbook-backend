package org.com.moodbook.book.dto;

import org.com.moodbook.book.entity.Book;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.common.constants.Language;

@Getter
@Builder
public class BookDTO {

  private Long id;
  private String isbn;
  private String title;
  private String author;
  private String publisher;
  private BigDecimal reputation;
  private String coverImage;
  private String description;
  private String genre;
  private int pageCount;
  private Language language;
  private LocalDate publishedAt;
  private BigDecimal recommendationTag;
  private Boolean isCached;

  public Book toEntity() {
    return Book.builder()
        .id(id)
        .isbn(isbn)
        .title(title)
        .author(author)
        .publisher(publisher)
        .reputation(reputation)
        .cover_image(coverImage)
        .description(description)
        .genre(genre)
        .page_count(pageCount)
        .language(language)
        .published_at(publishedAt)
        .recommendation_tag(recommendationTag)
        .is_cached(isCached)
        .build();
  }

  public static BookDTO toDTO(Book book) {
    return BookDTO.builder()
        .id(book.getId())
        .isbn(book.getIsbn())
        .title(book.getTitle())
        .author(book.getAuthor())
        .publisher(book.getPublisher())
        .reputation(book.getReputation())
        .coverImage(book.getCover_image())
        .description(book.getDescription())
        .genre(book.getGenre())
        .pageCount(book.getPage_count())
        .language(book.getLanguage())
        .publishedAt(book.getPublished_at())
        .recommendationTag(book.getRecommendation_tag())
        .isCached(book.getIs_cached())
        .build();
  }
}
