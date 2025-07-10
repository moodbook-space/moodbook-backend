package org.com.moodbook.emotion.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;
import org.com.moodbook.emotion.service.BookEmotionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookEmotionController {
  private final BookEmotionService bookEmotionService;

  @PostMapping("/api/emotion/save")
  public ResponseEntity<BookEmotionScoreResponse> saveEmotion(
      @RequestBody BookEmotionScoreRequest request) throws Exception {

    BookEmotionScoreResponse bookEmotionScore = bookEmotionService.saveEmotionScore(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookEmotionScore);
  }

}
