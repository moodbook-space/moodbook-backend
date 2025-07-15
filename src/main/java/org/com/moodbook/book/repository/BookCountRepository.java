package org.com.moodbook.book.repository;

import java.util.Optional;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCountRepository extends JpaRepository<BookCount, Long> {

    Optional<BookCount> findByBook(Book book);
}
