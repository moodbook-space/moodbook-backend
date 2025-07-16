package org.com.moodbook.post.repository;

import org.com.moodbook.post.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  Page<Report> findByBook_Id(Long bookId, Pageable pageable);

  // 마이페이지의 내 글 찾기 및 내 모임 찾기를 위해서 존재
  Page<Report> findByMember_Id(Long memberId, Pageable pageable);
}