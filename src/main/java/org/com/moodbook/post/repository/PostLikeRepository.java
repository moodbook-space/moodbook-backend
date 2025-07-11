package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  boolean existsByPostIdAndMemberId(Long postId, Long memberId);

  void deleteByPostIdAndMemberId(Long postId, Long memberId);
}