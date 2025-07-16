package org.com.moodbook.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeStatusResponse {
  private long likeCount;
  private boolean liked;
}
