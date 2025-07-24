package org.com.moodbook.book.dto;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BookEmotionRecommendAllRequest {

  private String emotionTag;
  private int page;     // 0부터 시작
  private int size;     // 한 페이지 크기
}
