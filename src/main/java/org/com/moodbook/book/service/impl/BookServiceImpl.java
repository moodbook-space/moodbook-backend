package org.com.moodbook.book.service.impl;

import static org.com.moodbook.common.exception.BaseException.BOOK_NOT_FOUND;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.BookEmotionAnalyzeResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendAllRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;

import org.com.moodbook.book.entity.QBook;
import org.com.moodbook.book.repository.BookCountRepository;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.repository.BookEmotionScoreRepository;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.com.moodbook.recentbookviews.repository.RecentBookViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
  private final MongoTemplate mongoTemplate;
  private final BookEmotionScoreRepository bookEmotionScoreRepository;
  private final MemberRepository memberRepository;
  private final RecentBookViewRepository recentBookViewRepository;

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
  public BookResponse getBookById(Long id, Long memberId) {
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

    // 1. 조회수 증가
    bookCount.increaseViewCount();
    bookCountRepository.save(bookCount);

    // 2. 최근 조회 기록 저장
    saveOrUpdateRecentView(memberId, book);

    return BookResponse.from(book, bookCount.getViewCount());
  }

  /** 책 조회수별 조회 **/
  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getTrendingBooks(Pageable pageable) {
    return bookRepository.findAllWithViewCount(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookEmotionRecommendResponse> getBooksByEmotionTop10(BookEmotionRecommendRequest request) {
    int minScore = 4;
    int maxScore = 5;
    int limit = 10;
    String emotionTag = request.getEmotionTag();

    System.out.println("emotionTag: " + emotionTag);
    System.out.println("Aggregation 조건: " + Criteria.where("scores." + emotionTag).gte(minScore).lte(maxScore));

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("scores." + emotionTag).gte(minScore).lte(maxScore)),
        Aggregation.sample(limit)
    );

    List<BookEmotionScore> emotionScores =
        mongoTemplate.aggregate(agg, "emotion_scores", BookEmotionScore.class).getMappedResults();

    System.out.println("emotionScores.size(): " + emotionScores.size());
    for (BookEmotionScore bes : emotionScores) {
      System.out.println("BookEmotionScore: " + bes.getBookTitle() + ", isbn13: " + bes.getIsbn13() + ", score: " + bes.getScores().get(emotionTag));
    }

    List<String> isbn13List = emotionScores.stream()
        .map(BookEmotionScore::getIsbn13)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());

    System.out.println("isbn13List: " + isbn13List);

    List<Book> books = bookRepository.findByIsbn13In(isbn13List);

    System.out.println("books.size(): " + books.size());
    for (Book b : books) {
      System.out.println("Book: " + b.getTitle() + ", isbn13: " + b.getIsbn13());
    }

    return books.stream()
        .map(BookEmotionRecommendResponse::from)
        .collect(Collectors.toList());
  }


  // 더보기
  @Override
  public List<BookEmotionRecommendAllResponse> getBooksByEmotionDesc(
      BookEmotionRecommendAllRequest request) {

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    String emotionTag = request.getEmotionTag();

    List<BookEmotionScore> scores = bookEmotionScoreRepository
        .findByEmotionScoreDesc(emotionTag, pageable);

    List<String> isbn13List = scores.stream()
        .map(BookEmotionScore::getIsbn13)
        .toList();

    List<Book> books = bookRepository.findByIsbn13In(isbn13List);

    return books.stream()
        .map(BookEmotionRecommendAllResponse::from)
        .toList();
  }

  private void saveOrUpdateRecentView(Long memberId, Book book) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    Optional<RecentBookView> existingView =
        recentBookViewRepository.findByMemberIdAndBookId(memberId, book.getId());

    if (existingView.isPresent()) {
      existingView.get().updateViewedAt(LocalDateTime.now());
    } else {
      RecentBookView view = RecentBookView.builder()
          .member(member)
          .book(book)
          .viewedAt(LocalDateTime.now())
          .build();
      recentBookViewRepository.save(view);
    }
  }

  // 분석을 위한 책에 관련된 책 소개 전체 조회
  public List<BookEmotionAnalyzeResponse> getAllBooksForEmotionAnalyze() {

    return bookRepository.findAll().stream()
        .map(BookEmotionAnalyzeResponse::from)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getAllBooks(Pageable pageable) {
    return bookRepository.findAllByCreatedAt(pageable);
  }


}
