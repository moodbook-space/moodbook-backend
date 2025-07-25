package org.com.moodbook.book.service;

import java.util.List;
import org.com.moodbook.book.dto.BookEmotionAnalyzeResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendAllRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

  List<BookResponse> saveAllBooks(List<Book> books);

  /**
   * 알라딘 평점 기준 추천
   **/
  Page<BookResponse> getRecommendedBooks(Pageable pageablem, Long memberId);

  /**
   * 책 상세 조회
   **/
  BookResponse getBookById(Long id, Long memberId);

  /**
   * 책 조회수별로 조회
   **/
  Page<BookResponse> getTrendingBooks(Pageable pageable, Long memberId);

  /**
   * 감정 별 책 추천 Top10
   **/
  List<BookEmotionRecommendResponse> getBooksByEmotionTop10(String emotion, Long memberId);

  /**
   * 감정 별 책 추천 내림차순
   **/
  List<BookEmotionRecommendAllResponse> getBooksByEmotionDesc(
      BookEmotionRecommendAllRequest request, Long memberId);

  /**
   * 감정 분석 후 책 전체 조회
   **/
  List<BookEmotionAnalyzeResponse> getAllBooksForEmotionAnalyze();

  /**
   * 책 전체 조회
   **/
  Page<BookResponse> getAllBooks(Pageable pageable, Long memberId);

  /** (관리자용) 책 검색어 조회 **/
  Page<BookResponse> getDbBookList(String query, Pageable pageable);

  /** (관리자용) 책 추가를 위한 알라딘 api 책 조회 **/
  List<BookResponse> searchForNewBook(String keyword);

  /** (관리자용) 조회된 책 db에 추가 **/
  boolean addBook(BookResponse bookResponse);

  /** (관리자용) db에서 책 제거 **/
  void deleteBookById(Long bookId);

}
