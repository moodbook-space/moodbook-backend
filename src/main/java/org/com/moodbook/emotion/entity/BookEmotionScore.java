package org.com.moodbook.emotion.entity;

import jakarta.persistence.Id;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "emotion_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookEmotionScore {

  @Id
  private String id;

  private Long bookId;
  private String isbn13;
  private String bookTitle;
  private String description; // 분석한 문장
  private Map<String, Integer> scores; // 감정별 점수
  private Long timestamp; // 저장 시간(선택)

}
