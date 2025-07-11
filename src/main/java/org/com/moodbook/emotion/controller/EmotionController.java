package org.com.moodbook.emotion.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;
import org.com.moodbook.emotion.service.BookEmotionService;
import org.com.moodbook.emotion.service.EmotionAnalyzer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionController {

  private final EmotionAnalyzer emotionAnalyzer;
  private final BookEmotionService bookEmotionService;

  @PostMapping("/analyze")
  public Map<String, Integer> analyzeEmotion(@RequestBody Map<String, String> request) throws Exception {
    String description = request.get("description");
    return emotionAnalyzer.analyzeEmotion(description);
  }


  @PostMapping("/save")
  public ResponseEntity<BookEmotionScoreResponse> saveEmotion(
      @RequestBody BookEmotionScoreRequest request) throws Exception {

    BookEmotionScoreResponse bookEmotionScore = bookEmotionService.saveEmotionScore(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookEmotionScore);
  }
}
