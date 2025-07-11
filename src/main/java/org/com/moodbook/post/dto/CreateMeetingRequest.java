package org.com.moodbook.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetingRequest {
  private String title;
  private String content;
  private String meetingType;      // 온라인, 오프라인, 하이브리드 enum 참고
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private int capacity;
  private String location;        // 오프라인 장소
  private List<Long> tagIds;      // mood tag ID 목록
}
