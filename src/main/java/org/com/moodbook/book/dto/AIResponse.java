package org.com.moodbook.book.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class AIResponse {
  private String mesaage;
  private List<BookResponse> isbn13;
}
