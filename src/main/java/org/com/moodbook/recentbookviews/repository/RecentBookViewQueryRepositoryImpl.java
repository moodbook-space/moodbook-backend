package org.com.moodbook.recentbookviews.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.entity.QBook;
import org.com.moodbook.book.entity.QBookCount;
import org.com.moodbook.recentbookviews.entity.QRecentBookView;
import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecentBookViewQueryRepositoryImpl implements RecentBookViewQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RecentBookView> findRecentViewsWithBookByMemberId(Long memberId,
        Pageable pageable) {

        QRecentBookView recentBookView = QRecentBookView.recentBookView;
        QBook book = QBook.book;
        QBookCount bookCount = QBookCount.bookCount;

        List<RecentBookView> content = queryFactory
            .selectFrom(recentBookView)
            .join(recentBookView.book, book).fetchJoin()                // Book 지연로딩 처리
            .leftJoin(book.bookCount, bookCount).fetchJoin()            // BookCount 지연로딩 처리
            .where(recentBookView.member.id.eq(memberId))
            .orderBy(recentBookView.viewedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long count = queryFactory
            .select(recentBookView.count())
            .from(recentBookView)
            .where(recentBookView.member.id.eq(memberId))
            .fetchOne();

        return new PageImpl<>(content, pageable, count != null ? count : 0L);
    }
}
