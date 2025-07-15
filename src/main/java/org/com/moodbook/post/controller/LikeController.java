package org.com.moodbook.post.controller;

import lombok.RequiredArgsConstructor;

import org.com.moodbook.post.dto.LikeStatusResponse;
import org.com.moodbook.post.service.LikeService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  /**
   * 좋아요 토글: 이미 좋아요가 있으면 취소, 없으면 생성
   * POST /api/posts/{postId}/like
   */
  @PostMapping("/{postId}/like")
  public ResponseEntity<Void> toggleLike(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    likeService.toggleLike(md.getId(), postId);
    return ResponseEntity.noContent().build();
  }

  /**
   * 좋아요 개수와 내가 눌렀는지 여부 조회
   * GET /api/posts/{postId}/like
   */
  @GetMapping("/{postId}/like")
  public ResponseEntity<LikeStatusResponse> getLikeStatus(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    boolean liked = likeService.isLikedBy(md.getId(), postId);
    long count = likeService.countLikes(postId);
    return ResponseEntity.ok(new LikeStatusResponse(count, liked));
  }
}
