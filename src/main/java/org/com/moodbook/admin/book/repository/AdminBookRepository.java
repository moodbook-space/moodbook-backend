package org.com.moodbook.admin.book.repository;

import org.com.moodbook.admin.book.dto.AdminBookDTO;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBookRepository extends JpaRepository<Book, Long> {

  @Query("SELECT new org.com.moodbook.admin.book.dto.AdminBookDTO(" +
      "c.id, c.isbn13, c.title, c.author, c.publisher, c.pubDate, c.coverImage, c.description, c.categoryName, c.createdAt )" +
      "FROM Book c " +
      "ORDER BY c.createdAt DESC")
  Page<AdminBookDTO> findAllPaging(Pageable pageable);


  @Query("SELECT new org.com.moodbook.admin.book.dto.AdminBookDTO(" +
      "c.id, c.isbn13, c.title, c.author, c.publisher, c.pubDate, c.coverImage, c.description, c.categoryName, c.createdAt) " +
      "FROM Book c " +
      "WHERE " +
      "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(c.isbn13) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(c.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(c.pubDate) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "LOWER(c.publisher) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
      "(c.description IS NOT NULL AND LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
      "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :query, '%')) " +
      "ORDER BY c.createdAt DESC")
  Page<AdminBookDTO> find(@Param("query") String query, Pageable pageable);

}
