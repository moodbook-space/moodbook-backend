package org.com.moodbook.emotion.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.dto.BookEmotionRecommendRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.dto.BookEmotionScoreResponse;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.service.BookEmotionService;
import org.com.moodbook.emotion.service.EmotionAnalyzer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final MongoTemplate mongoTemplate;
  private final BookService bookService;

  @PostMapping("/analyze")
  public Map<String, Integer> analyzeEmotion(@RequestBody Map<String, String> request) throws Exception {

    String description = request.get("description");
    if (description == null || description.trim().isEmpty()) {
      description = request.getOrDefault("title", "");
    }
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
