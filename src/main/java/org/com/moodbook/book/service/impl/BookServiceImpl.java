package org.com.moodbook.book.service.impl;

import static org.com.moodbook.common.exception.BaseException.BOOK_NOT_FOUND;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;

import org.com.moodbook.book.entity.QBook;
import org.com.moodbook.book.repository.BookCountRepository;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

  private final JPAQueryFactory queryFactory;
  private final BookRepository bookRepository;
  private final BookCountRepository bookCountRepository;

  /** Recommendation (기준: 알라딘 평점순 - 높은 순으로) **/
  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getRecommendedBooks(Pageable pageable) {
    QBook book = QBook.book;

    List<Book> content = queryFactory
        .selectFrom(book)
        .orderBy(
            book.reputation.desc().nullsLast(),         // 1차 정렬: 평점
            book.createdAt.desc()                       // 2차 정렬: 생성일
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // count 쿼리 생략 시에도 동작은 가능하지만 정확한 totalCount가 필요하면 아래 포함
    Long total = queryFactory
        .select(book.count())
        .from(book)
        .fetchOne();

    return PageableExecutionUtils.getPage(
        content.stream().map(BookResponse::from).toList(),
        pageable,
        () -> total != null ? total : 0L
    );
  }

  /** 도서 상세 조회 **/
  @Override
  @Transactional
  public BookResponse getBookById(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> BOOK_NOT_FOUND);

    BookCount bookCount = bookCountRepository.findByBook(book)
        .orElseGet(() -> {
          BookCount newCount = BookCount.builder()
              .book(book)
              .viewCount(0L)
              .build();
          return bookCountRepository.save(newCount);
        });

    // 4. 조회수 증가
    bookCount.increaseViewCount();
    bookCountRepository.save(bookCount);

    // 5. 업데이트된 조회수 반영해 BookResponse 새로 생성
    return BookResponse.from(book, bookCount.getViewCount());
  }

  /** 책 조회수별 조회 **/
  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getTrendingBooks(Pageable pageable) {
    return bookRepository.findAllWithViewCount(pageable);
  }

}
