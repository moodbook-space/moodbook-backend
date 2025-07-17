package org.com.moodbook.recentbookviews.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.recentbookviews.entity.RecentBookView;

@Getter
@Builder
@AllArgsConstructor
public class RecentBookViewResponse {

    private Long bookId;
    private String title;
    private String coverImage;
    private LocalDateTime viewedAt;

    public static RecentBookViewResponse fromEntity(RecentBookView recentBookView) {
        Book book = recentBookView.getBook();
        return RecentBookViewResponse.builder()
            .bookId(book.getId())
            .title(book.getTitle())
            .coverImage(book.getCoverImage())
            .viewedAt(recentBookView.getViewedAt())
            .build();
    }

}
