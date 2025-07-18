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
@Tag(name = "CommentController", description = "게시글 댓글에 대한 컨트롤러")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  /** 댓글/대댓글 작성 */
  @PostMapping("/comments")
  @Operation(summary = "댓글/대댓글 작성",
      description = "댓글/대댓글 작성을 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "댓글/대댓글 작성을 완료하였습니다."),
      @ApiResponse(responseCode = "500", description = "댓글/대댓글 작성에 실패하였습니다.")
  })
  public ResponseEntity<Long> addComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @Valid @RequestBody CreateCommentRequest req
  ) {
    Long id = commentService.addComment(md.getId(), req);
    return ResponseEntity.status(201).body(id);
  }

  /** 게시글 댓글(및 대댓글) 목록 조회 */
  @GetMapping("/posts/{postId}/comments")
  @Operation(summary = "댓글/대댓글 조회",
      description = "댓글/대댓글을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "댓글/대댓글 조회를 완료하였습니다."),
      @ApiResponse(responseCode = "500", description = "댓글/대댓글 조회에 실패하였습니다.")
  })
  public ResponseEntity<List<CommentResponse>> getComments(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long postId
  ) {
    return ResponseEntity.ok(commentService.getComments(md.getId(), postId));
  }

  /** 댓글(및 대댓글) 삭제 */
  @DeleteMapping("/comments/{id}")
  @Operation(summary = "댓글/대댓글 삭제",
      description = "댓글/대댓글을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "댓글/대댓글 삭제을 완료하였습니다."),
      @ApiResponse(responseCode = "500", description = "댓글/대댓글 삭제에 실패하였습니다.")
  })
  public ResponseEntity<Void> deleteComment(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable Long id
  ) {
    commentService.deleteComment(md.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
