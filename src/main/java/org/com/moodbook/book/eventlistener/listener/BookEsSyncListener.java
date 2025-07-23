package org.com.moodbook.book.eventlistener.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.com.moodbook.book.elasticsearch.repository.BookEsRepository;
import org.com.moodbook.book.eventlistener.event.BookCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BookEsSyncListener {

  private final BookEsRepository bookEsRepository;
  private final ElasticsearchClient esClient;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onBookCreated(BookCreatedEvent event) {
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
        System.out.println("book-index-v2 저장 완료:");
      } catch (Exception e) {
        System.err.println("book-index-v2 저장 오류: " + e.getMessage());
      }
    });
  }
}
