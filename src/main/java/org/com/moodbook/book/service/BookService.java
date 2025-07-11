package org.com.moodbook.book.service;

import java.util.List;
import org.com.moodbook.book.dto.BookRequest;
import org.com.moodbook.book.dto.BookResponse;

public interface BookService {

  public BookResponse saveBook(BookRequest bookRequest);
  public BookResponse updateBook(BookRequest bookRequest);
  public void deleteBookById(Long id);

  public BookResponse getBookById(Long id);
  public List<BookResponse> getAllBooks();
  public List<BookResponse> getBooksPopular();

  // 도서관련 리뷰 조회

  // 도서관련 게시글 조회
}
