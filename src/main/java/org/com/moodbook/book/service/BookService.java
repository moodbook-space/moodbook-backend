package org.com.moodbook.book.service;

import java.util.List;
import org.com.moodbook.book.dto.BookRequest;
import org.com.moodbook.book.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

  /** 알라딘 평점 기준 추천 **/
  Page<BookResponse> getRecommendedBooks(Pageable pageable);

  /** 책 상세 조회 **/
  BookResponse getBookById(Long id);

  /** 책 조회수별로 조회 **/
  Page<BookResponse> getTrendingBooks(Pageable pageable);

}
