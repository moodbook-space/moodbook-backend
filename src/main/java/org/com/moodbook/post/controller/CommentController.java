package org.com.moodbook.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.CommentResponse;
import org.com.moodbook.post.dto.CreateCommentRequest;
import org.com.moodbook.post.service.CommentService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "CommentController", description = "게시글 댓글 및 대댓글 기능")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  /** 댓글/대댓글 작성 */
  @PostMapping
  @Operation(summary = "댓글/대댓글 작성", description = "댓글 또는 대댓글을 작성합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "작성 성공"),
      @ApiResponse(responseCode = "404", description = "게시글 또는 부모 댓글을 찾을 수 없음")
  })
  public ResponseEntity<Long> addComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId,
      @Valid @RequestBody CreateCommentRequest req
  ) {
    req.setPostId(postId);
    Long id = commentService.addComment(md.getId(), req);
    return ResponseEntity.status(201).body(id);
  }

  /** 1) 최상위 댓글만 페이징(무한스크롤) */
  @GetMapping
  @Operation(summary = "최상위 댓글 목록 조회", description = "페이지 단위로 최상위 댓글을 조회합니다.")
  public ResponseEntity<List<CommentResponse>> getComments(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
    var comments = commentService.getComments(md.getId(), postId, pageable);
    return ResponseEntity.ok(comments);
  }

  /** 2) 특정 댓글의 답글 트리 전체 조회 */
  @GetMapping("/{parentCommentId}/replies")
  @Operation(summary = "답글 트리 조회", description = "대댓글 이상 모든 하위 댓글을 조회합니다.")
  public ResponseEntity<List<CommentResponse>> getReplies(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId,
      @PathVariable Long parentCommentId
  ) {
    var replies = commentService.getReplies(md.getId(), parentCommentId);
    return ResponseEntity.ok(replies);
  }

  /** 댓글/대댓글 삭제 */
  @DeleteMapping("/{commentId}")
  @Operation(summary = "댓글 삭제", description = "댓글과 모든 하위 대댓글을 삭제합니다.")
  public ResponseEntity<Void> deleteComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId,
      @PathVariable Long commentId
  ) {
    commentService.deleteComment(md.getId(), commentId);
    return ResponseEntity.noContent().build();
  }
}