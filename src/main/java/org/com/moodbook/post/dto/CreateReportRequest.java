package org.com.moodbook.post.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReportRequest {
  private String title;
  private String content;
  private Long bookId;
  private List<Long> tagIds; // mood tag ID 목록
}