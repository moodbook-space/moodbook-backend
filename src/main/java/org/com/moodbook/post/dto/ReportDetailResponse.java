package org.com.moodbook.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDetailResponse {
  private Long id;
  private String title;
  private String content;
  private Long bookId;
  private String bookTitle;
  private String bookAuthor;
  private List<String> tags;
  private String authorName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
