package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.BasePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasePostRepository extends JpaRepository<BasePost, Long> {

}