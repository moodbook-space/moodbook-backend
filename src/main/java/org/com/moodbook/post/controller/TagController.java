package org.com.moodbook.post.controller;


import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.TagResponse;
import org.com.moodbook.post.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  /**
   * 감정 태그 전체 조회 GET /api/tags
   */
  @GetMapping("/api/tags")
  public ResponseEntity<List<TagResponse>> getAllTags() {
    List<TagResponse> tags = tagService.getAllTags();
    return ResponseEntity.ok(tags);
  }
}
