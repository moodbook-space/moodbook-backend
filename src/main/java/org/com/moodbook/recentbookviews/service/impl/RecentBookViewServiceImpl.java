package org.com.moodbook.recentbookviews.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.recentbookviews.dto.RecentBookViewResponse;
import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.com.moodbook.recentbookviews.repository.RecentBookViewQueryRepository;
import org.com.moodbook.recentbookviews.repository.RecentBookViewRepository;
import org.com.moodbook.recentbookviews.service.RecentBookViewService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecentBookViewServiceImpl implements RecentBookViewService {

    private final RecentBookViewQueryRepository recentBookViewQueryRepository;

    /**
     * 회원별 최근 조회한 책 확인
     **/
    @Override
    @Transactional(readOnly = true)
    public Page<RecentBookViewResponse> getRecentBookViews(Long memberId, Pageable pageable) {
        if (memberId == null) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            Page<RecentBookView> recentViews =
                recentBookViewQueryRepository.findRecentViewsWithBookByMemberId(memberId, pageable);
            return recentViews.map(RecentBookViewResponse::fromEntity);
        } catch (DataAccessException e) {
            log.error("데이터베이스 접근 중 오류 발생: {}", e.getMessage(), e);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
