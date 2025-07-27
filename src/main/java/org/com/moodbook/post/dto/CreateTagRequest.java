package org.com.moodbook.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTagRequest {
  @NotBlank(message = "태그 이름은 비어 있을 수 없습니다.")
  private String name;
}