package org.com.moodbook.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.com.moodbook.post.dto.LikeStatusResponse;
import org.com.moodbook.post.service.LikeService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "LikeController", description = "게시글 좋아요에 대한 컨트롤러")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  /**
   * 좋아요 토글: 이미 좋아요가 있으면 취소, 없으면 생성
   */
  @PostMapping("/{postId}/like")
  @Operation(summary = "좋아요 토글",
      description = "이미 좋아요가 있으면 취소, 없으면 생성")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "좋아요가 생성되었습니다."),
      @ApiResponse(responseCode = "500", description = "좋아요 생성에 완벽하게 실패했습니다.")
  })
  public ResponseEntity<Void> toggleLike(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    likeService.toggleLike(md.getId(), postId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 좋아요 개수와 내가 눌렀는지 여부 조회
   */
  @GetMapping("/{postId}/like")
  @Operation(summary = "좋아요 개수와 내가 눌렀는지 여부 조회",
      description = "좋아요 개수 및 자신이 좋아요를 눌렀는지의 여부 확인")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "정보를 정상적으로 조회하였습니다."),
      @ApiResponse(responseCode = "500", description = "정보를 정상적으로 조회하는데 실패하였습니다.")
  })
  public ResponseEntity<LikeStatusResponse> getLikeStatus(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    boolean liked = likeService.isLikedBy(md.getId(), postId);
    long count = likeService.countLikes(postId);
    return ResponseEntity.ok(new LikeStatusResponse(count, liked));
  }
}
