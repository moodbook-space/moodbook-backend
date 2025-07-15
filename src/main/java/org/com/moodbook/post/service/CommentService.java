package org.com.moodbook.post.service;


import org.com.moodbook.post.dto.CommentResponse;
import org.com.moodbook.post.dto.CreateCommentRequest;

import java.util.List;

public interface CommentService {

  /**
   * 댓글 또는 대댓글 작성
   */
  Long addComment(Long memberId, CreateCommentRequest req);

  /**
   * 게시글의 댓글 및 대댓글 목록 조회
   */
  List<CommentResponse> getComments(Long memberId, Long postId);

  /**
   * 댓글 삭제 -> 대댓글도 함께 삭제됨
   */
  void deleteComment(Long memberId, Long commentId);
}
