package org.com.moodbook.recentbookviews.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.member.entity.Member;

@Entity
@Table(name = "recent_book_views")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentBookView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @Builder
    public RecentBookView(Member member, Book book, LocalDateTime viewedAt) {
        this.member = member;
        this.book = book;
        this.viewedAt = viewedAt;
    }

    public void updateViewedAt(LocalDateTime newViewedAt) {
        this.viewedAt = newViewedAt;
    }

}
