package org.com.moodbook.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.bookmark.entity.Bookmark;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponseDTO {
  private Long bookId;
  private String title;
  private String coverImage;
  private String description;
  private String categoryName;

  public static BookmarkResponseDTO of(Bookmark bookmark) {
    Book book = bookmark.getBook();
    return BookmarkResponseDTO.builder()
        .bookId(book.getId())
        .title(book.getTitle())
        .coverImage(book.getCoverImage())
        .description(book.getDescription())
        .categoryName(book.getCategoryName())
        .build();
  }
}
