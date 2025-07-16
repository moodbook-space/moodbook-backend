package org.com.moodbook.post.repository;


import org.com.moodbook.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  /**
   * 최상위 댓글만, postId 기준, 시간순 조회
   */
  List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId);

  /**
   * 특정 댓글의 대댓글 조회(시간순)
   */
  List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
}
