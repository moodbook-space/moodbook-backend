package org.com.moodbook.post.service;

import java.util.List;
import org.com.moodbook.post.dto.CommentResponse;
import org.com.moodbook.post.dto.CreateCommentRequest;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  /** 댓글 또는 대댓글 작성 */
  Long addComment(Long memberId, CreateCommentRequest req);

  /** 최상위 댓글만 페이징 조회 */
  List<CommentResponse> getComments(Long memberId, Long postId, Pageable pageable);

  /** 특정 댓글 이하의 모든 하위 댓글 트리 조회 */
  List<CommentResponse> getReplies(Long memberId, Long parentCommentId);

  /** 댓글 및 하위 댓글 삭제 */
  void deleteComment(Long memberId, Long commentId);
}