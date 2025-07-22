package org.com.moodbook.book.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AIResponse {
  private String mesaage;
  private List<BookResponse> isbn13;
}
