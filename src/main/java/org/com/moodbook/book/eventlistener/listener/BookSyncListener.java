package org.com.moodbook.book.eventlistener.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.com.moodbook.book.elasticsearch.repository.BookEsRepository;
import org.com.moodbook.book.eventlistener.event.BookCreatedEvent;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.service.BookEmotionService;
import org.com.moodbook.emotion.service.EmotionAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class BookSyncListener {

  private final BookEsRepository bookEsRepository;
  private final ElasticsearchClient esClient;

  private final EmotionAnalyzer emotionAnalyzer;
  private final BookEmotionService bookEmotionService;

  // DB -> ES
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookCreatedES(BookCreatedEvent event) {
    event.getBooks().forEach(book -> {
      // Book -> BookEsDocument 변환 후 저장
      BookEsDocument esDoc = BookEsDocument.fromEntity(book);
      bookEsRepository.save(esDoc);

      // v2 인덱스에 별도로 저장
      try {
        esClient.index(i -> i
            .index("book-index-v2")
            .id(esDoc.getBookId().toString())
            .document(esDoc)
        );
        log.info("book-index-v2 저장 완료:");
      } catch (Exception e) {
        log.error("book-index-v2 저장 오류: {}", e.getMessage());
      }
    });
  }

  // DB -> Mongo
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookCreatedMongo(BookCreatedEvent event) {
    event.getBooks().forEach(book -> {
      try {
        BookEmotionScoreRequest req = BookEmotionScoreRequest.builder()
            .bookId(book.getId())
            .isbn13(book.getIsbn13())
            .bookTitle(book.getTitle())
            .description(book.getDescription())
            .build();

        // 감정 분석 및 Mongo 저장까지 내부에서 처리
        bookEmotionService.saveEmotionScore(req);
        log.info("감정 분석 및 저장 성공 : 책이름 - {}", req.getBookTitle());


      } catch (Exception e) {
        // 에러 핸들링
        log.warn("감정 분석 및 저장 실패: {}", book.getIsbn13(), e);
      }
    });
  }



}
