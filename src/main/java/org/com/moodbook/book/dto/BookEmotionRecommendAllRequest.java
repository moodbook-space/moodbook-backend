package org.com.moodbook.book.dto;

import lombok.Getter;

@Getter
public class BookEmotionRecommendAllRequest {
  private String emotionTag;
  private int page;     // 0부터 시작
  private int size;     // 한 페이지 크기

}
