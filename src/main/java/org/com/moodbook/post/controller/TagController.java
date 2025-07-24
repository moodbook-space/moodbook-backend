package org.com.moodbook.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.CreateTagRequest;
import org.com.moodbook.post.dto.TagResponse;
import org.com.moodbook.post.service.TagService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
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
  @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
    List<TagResponse> tags = tagService.getAllTags();
    return ResponseEntity.ok(tags);
  }

  /**
   * 감정 태그 생성
   */
  @Operation(summary = "감정 태그 생성",
      description = "새로운 감정 태그를 생성합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "감정 태그 생성에 성공하였습니다."),
      @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
      @ApiResponse(responseCode = "500", description = "감정 태그 생성에 실패하였습니다.")
  })
  @PostMapping
  public ResponseEntity<TagResponse> createTag(
      @RequestBody @Validated CreateTagRequest request,
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {
    TagResponse created = tagService.createTag(request);
    return ResponseEntity.status(201).body(created);
  }
}
