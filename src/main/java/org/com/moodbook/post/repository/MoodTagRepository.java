package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.MoodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoodTagRepository extends JpaRepository<MoodTag, Long> {

  Optional<MoodTag> findByName(String name);
}