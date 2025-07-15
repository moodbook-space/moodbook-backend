package org.com.moodbook.post.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
  private Long id;
  private Long authorId;
  private String authorName;
  private String content;
  private LocalDateTime createdAt;
  private boolean isMine;
  private List<CommentResponse> replies;
}