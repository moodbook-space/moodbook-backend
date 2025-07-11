package org.com.moodbook.book.repository;

import java.util.List;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
  List<Book> findAllByOrderByReputationDesc();

}
