package org.com.moodbook.book.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.book.entity.Book;

@Getter
@NoArgsConstructor
@Builder
public class BookResponse {

    private Long bookId;
    private String isbn13;
    private String title;
    private String author;
    private String publisher;
    private String pubDate;
    private BigDecimal reputation;
    private String coverImage;
    private String description;
    private String categoryName;
    private LocalDateTime createdAt;
    private Long viewCount;

    public static BookResponse from(Book book, Long viewCount) {
        return new BookResponse(
            book.getId(),
            book.getIsbn13(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublisher(),
            book.getPubDate(),
            book.getReputation(),
            book.getCoverImage(),
            book.getDescription(),
            book.getCategoryName(),
            book.getCreatedAt(),
            viewCount != null ? viewCount : 0L
        );
    }

    public BookResponse(Long bookId, String isbn13, String title, String author, String publisher,
        String pubDate, BigDecimal reputation, String coverImage, String description,
        String categoryName, LocalDateTime createdAt, Long viewCount) {
        this.bookId = bookId;
        this.isbn13 = isbn13;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.reputation = reputation;
        this.coverImage = coverImage;
        this.description = description;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
    }

    public static BookResponse from(Book book) {
        return from(book, 0L);
    }

}
