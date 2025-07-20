package org.com.moodbook.recentbookviews.repository;

import java.util.Optional;
import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentBookViewRepository extends JpaRepository<RecentBookView, Long> {

    Optional<RecentBookView> findByMemberIdAndBookId(Long userId, Long bookId);

//    Page<RecentBookView> findByMemberId(Long memberId, Pageable pageable);

//    Page<RecentBookView> findRecentViewsWithBookByMemberId(Long memberId, Pageable pageable);

}
