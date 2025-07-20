package org.com.moodbook.recentbookviews.repository;

import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecentBookViewQueryRepository {
    /* N + 1 문제 처리 */
    Page<RecentBookView> findRecentViewsWithBookByMemberId(Long memberId, Pageable pageable);
}
