package org.com.moodbook.post.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "LikeController", description = "게시글 좋아요 기능")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;


  @PostMapping("/{postId}/like")
  @Operation(summary = "좋아요 토글", description = "이미 좋아요가 있으면 취소, 없으면 생성")
  public ResponseEntity<Void> toggleLike(
      @AuthenticationPrincipal CustomMemberDetails user,
      @PathVariable Long postId
  ) {
    likeService.toggleLike(user.getId(), postId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{postId}/like")
  @Operation(summary = "좋아요 상태 조회", description = "총 좋아요 수와 내가 눌렀는지 여부 반환")
  public ResponseEntity<LikeStatusResponse> getLikeStatus(
      @AuthenticationPrincipal CustomMemberDetails user,
      @PathVariable Long postId
  ) {
    long count = likeService.countLikes(postId);
    boolean liked = likeService.isLikedBy(user.getId(), postId);
    return ResponseEntity.ok(new LikeStatusResponse(count, liked));
  }
}
