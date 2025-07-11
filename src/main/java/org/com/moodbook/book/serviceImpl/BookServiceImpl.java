package org.com.moodbook.book.serviceImpl;

import static org.com.moodbook.common.exception.ErrorCode.BOOK_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.dto.BookRequest;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.common.exception.BaseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;


  @Override
  public BookResponse saveBook(BookRequest bookRequest) {
    return null;
  }

  @Override
  public BookResponse updateBook(BookRequest bookRequest) {
    return null;
  }

  @Override
  public void deleteBookById(Long id) {
    bookRepository.deleteById(id);
  }

  @Override
  public BookResponse getBookById(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BaseException(BOOK_NOT_FOUND));
    return new BookResponse(book);
  }


  @Override
  public List<BookResponse> getAllBooks() {
    List<Book> books = bookRepository.findAll();
    return books.stream().map(BookResponse::new).collect(Collectors.toList());
  }

  @Override
  public List<BookResponse> getBooksPopular() { // reputation 기준 내림차순
    List<Book> books = bookRepository.findAllByOrderByReputationDesc();
    return books.stream()
        .map(BookResponse::fromEntity)
        .collect(Collectors.toList());
  }
}
