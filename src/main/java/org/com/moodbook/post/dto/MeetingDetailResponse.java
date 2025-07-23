package org.com.moodbook.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingDetailResponse {

  private Long id;
  private String title;
  private String content;
  private String meetingType;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
  private int capacity;
  private String location;
  private int viewCount;
  private int likeCount;
  private int currentParticipants;
  private List<String> tags;
  private String hostName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean likedByMe;
  private Long chatRoomId;
}
