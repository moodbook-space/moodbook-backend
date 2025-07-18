package org.com.moodbook.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.TagResponse;
import org.com.moodbook.post.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "TagController", description = "감정 태그에 대한 컨트롤러")
public class TagController {

  private final TagService tagService;

  /**
   * 감정 태그 전체 조회 GET /api/tags
   */
  @Operation(summary = "감정 태그 전체 조회",
      description = "감정 태그 전체를 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "감정 태그 전체 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "감정 태그 전체 조회에 실패하였습니다.")
  })
  @GetMapping("/api/tags")
  public ResponseEntity<List<TagResponse>> getAllTags() {
    List<TagResponse> tags = tagService.getAllTags();
    return ResponseEntity.ok(tags);
  }
}
