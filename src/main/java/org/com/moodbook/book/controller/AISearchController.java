package org.com.moodbook.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.dto.AIRequest;
import org.com.moodbook.book.dto.AIResponse;
import org.com.moodbook.book.service.AiSearchService;
import org.com.moodbook.book.service.impl.AiSearchServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/openai")
public class AISearchController {

  private final AiSearchService aiSearchService;

  @Operation(summary = "ai검색을 통한 책 추천",description = "openai의 response를 이용해 db에 없던 책은 자동으로 db에 추가까지 해줍니다 ")
  @PostMapping("/ask")
  public ResponseEntity<AIResponse> askOpenAi(@RequestBody AIRequest request) {
    AIResponse response = aiSearchService.askQuestion(request.getPrompt());
    return ResponseEntity.ok(response);
  }
}
