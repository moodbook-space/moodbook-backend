package org.com.moodbook.post.dto;


import jakarta.validation.constraints.NotBlank;
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
public class UpdateReportRequest {

  @NotBlank
  private String title;

  @NotBlank
  private String content;

  private Long bookId;

  private List<Long> tagIds;
}