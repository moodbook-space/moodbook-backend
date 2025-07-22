package org.com.moodbook.emotion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "EmotionController", description = "감정 분석을 다루기 위한 컨트롤러")
public class EmotionController {

  private final EmotionAnalyzer emotionAnalyzer;
  private final BookEmotionService bookEmotionService;
  private final MongoTemplate mongoTemplate;
  private final BookService bookService;

  @Operation(summary = "감정 분석 기능 제공하여 데이터 추가", description = "")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "감정 분석 "),
      @ApiResponse(responseCode = "500", description = "")
  })
  @GetMapping("/analyze")
  public Map<String, Integer> analyzeEmotion(@RequestBody Map<String, String> request) throws Exception {

    String description = request.get("description");
    if (description == null || description.trim().isEmpty()) {
      description = request.getOrDefault("title", "");
    }
    return emotionAnalyzer.analyzeEmotion(description);
  }

  @Operation(summary = "감정 저장", description = "")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "감정 저장에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "감정 저장에 실패하였습니다.")
  })
  @PostMapping("/save")
  public ResponseEntity<BookEmotionScoreResponse> saveEmotion(
      @RequestBody BookEmotionScoreRequest request) throws Exception {

    BookEmotionScoreResponse bookEmotionScore = bookEmotionService.saveEmotionScore(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookEmotionScore);
  }
}
