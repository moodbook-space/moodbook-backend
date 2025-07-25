package org.com.moodbook.book.service.impl;

import static org.com.moodbook.common.exception.BaseException.BOOK_NOT_FOUND;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;
import org.com.moodbook.book.entity.QBook;
import org.com.moodbook.book.eventlistener.event.BookCreatedEvent;
import org.com.moodbook.book.repository.BookCountRepository;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.common.config.AladinApiProperties;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.emotion.entity.BookEmotionScore;
import org.com.moodbook.emotion.repository.BookEmotionScoreRepository;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.recentbookviews.entity.RecentBookView;
import org.com.moodbook.recentbookviews.repository.RecentBookViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
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
  private final AladinApiProperties aladinApiProperties;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Override
  public List<BookResponse> saveAllBooks(List<Book> books) {
    bookRepository.saveAll(books);
    publisher.publishEvent(new BookCreatedEvent(books));

    return books.stream().map(BookResponse::from).collect(Collectors.toList());

  }

  /**
   * Recommendation (기준: 알라딘 평점순 - 높은 순으로)
   **/
  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getRecommendedBooks(Pageable pageable, Long memberId) {
    QBook book = QBook.book;

    // 로그인 안 한 경우 처리
    if (memberId == null) {
      throw BaseException.UNAUTHORIZED_ACCESS;
    }

    List<Book> content = queryFactory
        .selectFrom(book)
        .leftJoin(book.bookCount).fetchJoin()
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

  /**
   * 도서 상세 조회
   **/
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

  /**
   * 책 조회수별 조회
   **/
  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getTrendingBooks(Pageable pageable, Long memberId) {
    if (memberId == null) {
      throw BaseException.UNAUTHORIZED_ACCESS;
    }

    return bookRepository.findAllWithViewCount(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookEmotionRecommendResponse> getBooksByEmotionTop10(String emotion, Long memberId) {
    if (memberId == null) {
      throw BaseException.UNAUTHORIZED_ACCESS;
    }

    int minScore = 4;
    int maxScore = 5;
    int limit = 10;

    Aggregation agg = Aggregation.newAggregation(
        Aggregation.match(Criteria.where("scores." + emotion).gte(minScore).lte(maxScore)),
        Aggregation.sample(limit)
    );

    List<BookEmotionScore> emotionScores =
        mongoTemplate.aggregate(agg, "emotion_scores", BookEmotionScore.class).getMappedResults();

    List<String> isbn13List = emotionScores.stream()
        .map(BookEmotionScore::getIsbn13)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());

    List<Book> books = bookRepository.findByIsbn13In(isbn13List);

    return books.stream()
        .map(BookEmotionRecommendResponse::from)
        .collect(Collectors.toList());
  }


  // 더보기
  @Override
  public List<BookEmotionRecommendAllResponse> getBooksByEmotionDesc(
      BookEmotionRecommendAllRequest request, Long memberId) {
    if (memberId == null) {
      throw BaseException.UNAUTHORIZED_ACCESS;
    }

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

    return bookRepository.findAllBooks().stream()
        .map(BookEmotionAnalyzeResponse::from)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BookResponse> getAllBooks(Pageable pageable, Long memberId) {
    if (memberId == null) {
      throw BaseException.UNAUTHORIZED_ACCESS;
    }

    return bookRepository.findAllByCreatedAt(pageable);
  }

  /** 관리자용 책 전체 조회 및 query 조회 **/
  @Override
  public Page<BookResponse> getDbBookList(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      return bookRepository.findAllByCreatedAt(pageable); // 기존 전체 목록
    }
    return bookRepository.findWithQuery(query, pageable);
  }

  /** (관리자용) 책 추가를 위한 알라딘 api 책 조회 **/
  public List<BookResponse> searchForNewBook(String title) {
    try {
      String apiUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx"
          + "?ttbkey=" + aladinApiProperties.getKey()
          + "&Query=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
          + "&QueryType=Title"
          + "&SearchTarget=Book"
          + "&output=js"
          + "&Version=20131101"
          + "&MaxResults=10";

      HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
      conn.setRequestMethod("GET");

      if (conn.getResponseCode() != 200) {
        return Collections.emptyList();
      }

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      reader.close();

      String jsonString = sb.toString()
          .replaceFirst("TTB_ItemSearch\\(", "")
          .replaceFirst("\\);?$", "");

      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
      JsonArray items = jsonObject.getAsJsonArray("item");
      if (items == null || items.isEmpty()) {
        return Collections.emptyList();
      }

      List<BookResponse> books = new ArrayList<>();
      for (JsonElement item : items) {
        JsonObject obj = item.getAsJsonObject();
        BookResponse book = BookResponse.builder()
            .title(obj.get("title").getAsString())
            .isbn13(obj.get("isbn13").getAsString())
            .author(obj.get("author").getAsString())
            .publisher(obj.get("publisher").getAsString())
            .pubDate(obj.get("pubDate").getAsString())
            .reputation(BigDecimal.valueOf(obj.get("customerReviewRank").getAsInt()))
            .coverImage(obj.get("cover").getAsString())
            .description(obj.get("description").getAsString())
            .categoryName(obj.get("categoryName").getAsString())
            .build();
        books.add(book);
      }

      return books;

    } catch (Exception e) {
      log.warn("알라딘 제목 검색 실패: {}", e.getMessage());
      return Collections.emptyList();
    }
  }


  /** (관리자용) 조회된 책 db에 추가 **/
  @Override
  public boolean addBook(BookResponse bookResponse){

    if(bookRepository.findByIsbn13(bookResponse.getIsbn13())==null) {
      Book book = Book.builder()
          .isbn13(bookResponse.getIsbn13())
          .title(bookResponse.getTitle())
          .author(bookResponse.getAuthor())
          .publisher(bookResponse.getPublisher())
          .pubDate(bookResponse.getPubDate())
          .reputation(bookResponse.getReputation())
          .coverImage(bookResponse.getCoverImage())
          .description(bookResponse.getDescription())
          .categoryName(bookResponse.getCategoryName())
          .build();

      bookRepository.save(book);
      return true;
    }
    else{
      return false;
    }

  }

  /** (관리자용) db에서 책 제거 **/
  @Override
  public void deleteBookById(Long bookId){
    if(!bookRepository.existsById(bookId)){
      throw new BaseException(ErrorCode.BOOK_NOT_FOUND);
    }
    bookRepository.deleteById(bookId);
  }

}
