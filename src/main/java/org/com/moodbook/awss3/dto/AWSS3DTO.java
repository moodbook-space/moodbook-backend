package org.com.moodbook.awss3.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AWSS3DTO {

  private String url;

  public static AWSS3DTO of(String url) {
    return AWSS3DTO.builder()
        .url(url)
        .build();
  }
}
