package org.com.moodbook.post.repository;

import java.util.List;
import org.com.moodbook.post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  // 페이징 가능한 최상위 댓글 조회
  Page<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

  // 대댓글 이상 전체 트리 조회의 시작점
  List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
}