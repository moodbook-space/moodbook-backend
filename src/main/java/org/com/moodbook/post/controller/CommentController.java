package org.com.moodbook.post.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.CommentResponse;
import org.com.moodbook.post.dto.CreateCommentRequest;
import org.com.moodbook.post.service.CommentService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  /** 댓글/대댓글 작성 */
  @PostMapping("/comments")
  public ResponseEntity<Long> addComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @Valid @RequestBody CreateCommentRequest req
  ) {
    Long id = commentService.addComment(md.getId(), req);
    return ResponseEntity.status(201).body(id);
  }

  /** 게시글 댓글(및 대댓글) 목록 조회 */
  @GetMapping("/posts/{postId}/comments")
  public ResponseEntity<List<CommentResponse>> getComments(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    return ResponseEntity.ok(commentService.getComments(md.getId(), postId));
  }

  /** 댓글(및 대댓글) 삭제 */
  @DeleteMapping("/comments/{id}")
  public ResponseEntity<Void> deleteComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long id
  ) {
    commentService.deleteComment(md.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
