package org.com.moodbook.post.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.post.entity.Comment;

@Getter
@Builder
public class CommentResponse {
  private Long id;
  private Long authorId;
  private String authorName;
  private String content;
  private LocalDateTime createdAt;
  private boolean isMine;
  private List<CommentResponse> replies;

  public static CommentResponse withoutReplies(Comment c, Long memberId) {
    return CommentResponse.builder()
        .id(c.getId())
        .authorId(c.getAuthor().getId())
        .authorName(c.getAuthor().getName())
        .content(c.getContent())
        .createdAt(c.getCreatedAt())
        .isMine(c.getAuthor().getId().equals(memberId))
        .replies(Collections.emptyList())
        .build();
  }
}