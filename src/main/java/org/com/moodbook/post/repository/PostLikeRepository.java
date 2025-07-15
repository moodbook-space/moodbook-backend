package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  /**
   * 해당 게시글에 특정 회원이 좋아요를 눌렀는지 확인
   */
  boolean existsByPost_IdAndMember_Id(Long postId, Long memberId);

  /**
   * 해당 게시글의 좋아요 개수 조회
   */
  long countByPost_Id(Long postId);

  /**
   * 해당 게시글에 대한 특정 회원의 좋아요 삭제
   */
  void deleteByPost_IdAndMember_Id(Long postId, Long memberId);
}
