package org.com.moodbook.post.repository;

import java.util.List;
import org.com.moodbook.post.entity.Meeting;
import org.com.moodbook.post.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
  Page<Meeting> findByMember_Id(Long memberId, Pageable pageable);

  @Override
  @EntityGraph(attributePaths = "moodTags")
  List<Meeting> findAll();

}