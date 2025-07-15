package org.com.moodbook.book.repository;

import java.util.List;
import java.util.Optional;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

  @Query("SELECT b.isbn13 FROM Book b WHERE b.isbn13 IN :isbn13List")
  List<String> findAllIsbn13In(@Param("isbn13List") List<String> isbn13List);

  @Query("""
        SELECT new org.com.moodbook.book.dto.BookResponse(
            b.id, b.isbn13, b.title, b.author, b.publisher, b.pubDate,
            b.reputation, b.coverImage, b.description, b.categoryName,
            b.createdAt, coalesce(bc.viewCount, 0)
        )
        FROM Book b
        LEFT JOIN b.bookCount bc 
        WHERE b.id = :bookId
    """)
  Optional<BookResponse> findBookDetail(@Param("bookId") Long bookId);

  @Query("""
        SELECT new org.com.moodbook.book.dto.BookResponse(
            b.id, b.isbn13, b.title, b.author, b.publisher, b.pubDate,
            b.reputation, b.coverImage, b.description, b.categoryName,
            b.createdAt, coalesce(bc.viewCount, 0)
        )
        FROM Book b
        LEFT JOIN BookCount bc ON b.id = bc.book.id
        ORDER BY COALESCE(bc.viewCount, 0) DESC
    """)
  Page<BookResponse> findAllWithViewCount(Pageable pageable);

  List<Book> findAllByOrderByReputationDesc();

}
