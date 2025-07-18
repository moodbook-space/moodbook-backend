package org.com.moodbook.admin.book.dto;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AdminBookDTO {

  private Long Id;
  private String isbn13;
  private String title;
  private String author;
  private String publisher;
  private String pubDate;
  private String coverImage;
  private String description;
  private String categoryName;
  private LocalDateTime createdAt;
}
