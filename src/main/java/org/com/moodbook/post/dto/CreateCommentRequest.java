package org.com.moodbook.post.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CreateCommentRequest {

  private Long postId;
  private Long parentCommentId;  // null 이면 댓글

  @NotBlank
  private String content;
}