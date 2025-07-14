package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}