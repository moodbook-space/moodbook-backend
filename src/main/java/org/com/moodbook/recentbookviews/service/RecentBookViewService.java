package org.com.moodbook.recentbookviews.service;

import org.com.moodbook.recentbookviews.dto.RecentBookViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecentBookViewService {
    Page<RecentBookViewResponse> getRecentBookViews(Long memberId, Pageable pageable);
}
