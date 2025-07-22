package org.com.moodbook.book.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.com.moodbook.book.elasticsearch.repository.BookEsRepository;
import org.com.moodbook.book.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookEsService {

  private final BookEsRepository bookEsRepository;
  private final BookRepository bookRepository;
  private final ElasticsearchClient client;

  @PostConstruct
  public void initIndex() {
    // 이미 색인이 되어 있다면 다시 돌리지 않도록 조건을 걸어줄 수도 있습니다.
    long esCount = bookEsRepository.count();
    if (esCount == 0) {
      indexAllReports();
    }
  }

  // db에 있는 책 ES에 전체 저장
  public void indexAllReports() {
    var docs = bookRepository.findAll().stream()
        .map(BookEsDocument::fromEntity)
        .toList();
    bookEsRepository.saveAll(docs);
  }

  // ES에 있는 책 모두 조회
  public Page<BookEsDocument> searchAllBooks(Pageable pageable) {
    return bookEsRepository.findAll(pageable);
  }

  public Page<BookEsDocument> search(String keyword, int page, int size) {
    // 엘라스틱서치에서 페이징을 위한 시작 위치를 계산하는 변수
    int from = page * size;

    // 엘라스틱서치에서 사용할 검색조건을 담는 객체
    Query query;

    try {
      // 검색어가 없으면 모든 문서를 검색하는 matchAll쿼리
      if (keyword == null || keyword.isBlank()) {
        query = MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
        //MatchAllQuery는 엘라스틱서치에서 조건 없이 모든 문서를 검색할 때 사용하는 쿼리
      }
      // 검색어가 있을 때
      else {
        // boolquery는 복수 조건을 조합할 때 사용하는 쿼리
        query = BoolQuery.of(b -> {
          b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());

          // 중간단어
          b.should(PrefixQuery.of(p -> p.field("title.ngram").value(keyword))._toQuery());

          // 오타
          if (keyword.length()>=3){
            b.should(
                MatchQuery.of(m ->m.field("title").query(keyword).fuzziness("AUTO"))._toQuery());
          }

          return b;
        })._toQuery();
      }

      SearchRequest request = SearchRequest.of(s->s
          .index("book-index")
          .from(from)
          .size(size)
          .query(query)
      );

      // 엘라스틱서치의 응답 결과를 담고있는 응답 객체
      SearchResponse<BookEsDocument> response =
          // 엘라스틱서치에 명령을 전달하는 자바 API 검색 요청을 담아서 응답객체로 반환
          client.search(request, BookEsDocument.class);

      // 위 응답객체에서 받은 검색 결과 중 문서만 추출해서 리스트로 만듬
      // Hit는 엘라스틱서치에서 검색된 문서 1개를 감싸고 있는 객체
      List<BookEsDocument> content = response.hits() // 엘라스틱서치 응답에서 hits(문서 검색 결과) 전체를 꺼냄
          .hits() // 검색 결과 안에 개별 리스트를 가져옴
          .stream() // 자바 stream api를 사용
          .map(Hit::source) // 각 Hit 객체에서 실제 문서를 꺼내는 작업
          .collect(Collectors.toList()); // 위에서 꺼낸 객체를 자바 List에 넣음

      // 전체 검색 결과 수
      long total = response.hits().total().value();

      return new PageImpl<>(content, PageRequest.of(page, size), total);



    } catch (IOException e) {
      log.error("검색 오류", e);
      throw new RuntimeException("검색 중 오류 발생", e);
    }
  }

  public List<String> autocomplete(String keyword) {
    try {
      Query query = MatchQuery.of(m -> m.field("title.autocomplete").query(keyword))._toQuery();
      SearchRequest request = SearchRequest.of(s -> s
          .index("book-index-v2")
          .query(query)
          .size(10) // 최대 10개만
      );

      SearchResponse<BookEsDocument> response = client.search(request, BookEsDocument.class);
      System.out.println(response.hits());

      return response.hits().hits().stream()
          .map(Hit::source)
          .filter(Objects::nonNull)
          .map(BookEsDocument::getTitle)
          .distinct()
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("자동완성 검색 오류", e);
    }
  }



}
