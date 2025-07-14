package org.com.moodbook.post.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMeetingRequest {
  private String title;
  private String content;
  private String meetingType;    // "ONLINE", "OFFLINE", "HYBRID"
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private int capacity;
  private String location;
  private List<Long> tagIds;     // 변경할 moodTag ID 목록
}