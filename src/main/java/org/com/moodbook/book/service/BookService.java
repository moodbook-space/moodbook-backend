package org.com.moodbook.book.service;

import java.util.List;
import org.com.moodbook.book.dto.BookEmotionAnalyzeResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendAllRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Bool;

public interface BookService {

  List<BookResponse> saveAllBooks(List<Book> books);

  /** 알라딘 평점 기준 추천 **/
  Page<BookResponse> getRecommendedBooks(Pageable pageable);

  /** 책 상세 조회 **/
  BookResponse getBookById(Long id, Long memberId);

  /** 책 조회수별로 조회 **/
  Page<BookResponse> getTrendingBooks(Pageable pageable);

  /** 감정 별 책 추천 Top10 **/
  List<BookEmotionRecommendResponse> getBooksByEmotionTop10(BookEmotionRecommendRequest request);

  /** 감정 별 책 추천 내림차순 **/
  List<BookEmotionRecommendAllResponse> getBooksByEmotionDesc(BookEmotionRecommendAllRequest request);

  /** 감정 분석 후 책 전체 조회 **/
  List<BookEmotionAnalyzeResponse> getAllBooksForEmotionAnalyze();

  /** 책 전체 조회 **/
  Page<BookResponse> getAllBooks(Pageable pageable);

  }
