package org.com.moodbook.post.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSummaryResponse {

  private Long id;
  private String title;
  private String authorName;
  private LocalDateTime createdAt;
  private int viewCount;
  private int likeCount;
  private List<String> tags;
  private boolean likedByMe;
}
