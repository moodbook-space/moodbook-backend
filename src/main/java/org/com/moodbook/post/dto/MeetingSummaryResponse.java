package org.com.moodbook.post.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingSummaryResponse {
  private Long id;
  private String title;
  private String hostName;
  private String meetingType;
  private LocalDateTime startAt;
  private int currentParticipants;
  private int capacity;
  private int viewCount;
  private int likeCount;
  private List<String> tags;
  private LocalDateTime createdAt;
}