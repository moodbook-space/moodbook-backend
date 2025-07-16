package org.com.moodbook.post.service;

public interface LikeService {

  /**
   * 좋아요: 이미 눌렀으면 취소, 아니면 좋아요 생성
   */
  void toggleLike(Long memberId, Long postId);

  /**
   * 하나의 게시글의 총 좋아요 개수 반환
   */
  long countLikes(Long postId);

  /**
   * 특정 유저가 해당 게시글을 좋아요했는지 여부 반환
   */
  boolean isLikedBy(Long memberId, Long postId);
}
