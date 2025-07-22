package org.com.moodbook.post.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.post.search.document.MeetingEsDocument;

import org.com.moodbook.post.repository.MeetingRepository;
import org.com.moodbook.post.search.repository.MeetingEsRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeetingEsService {

  private final MeetingEsRepository meetingEsRepository;
  private final MeetingRepository meetingRepository;
  private final ElasticsearchClient client;

  @Transactional(readOnly = true)
  @PostConstruct
  public void initIndex() {
    if (meetingEsRepository.count() == 0) {
      indexAllMeetings();
    }
  }

  /** DB에 있는 모든 Meeting을 ES에 색인 */
  @Transactional
  public void indexAllMeetings() {
    List<MeetingEsDocument> docs = meetingRepository.findAll().stream()
        .map(MeetingEsDocument::fromEntity)
        .collect(Collectors.toList());
    meetingEsRepository.saveAll(docs);
  }

  /** ES에 색인된 모든 Meeting 문서 페이지 조회 */
  public Page<MeetingEsDocument> searchAllMeetings(Pageable pageable) {
    return meetingEsRepository.findAll(pageable);
  }

  /**
   * 키워드로 검색:
   * - blank → MatchAll
   * - 그 외 → BoolQuery:
   *     • title.autocomplete (prefix)
   *     • title.ngram        (부분문자열)
   */
  public Page<MeetingEsDocument> search(String keyword, int page, int size) {
    int from = page * size;
    Query query;

    if (keyword == null || keyword.isBlank()) {
      query = MatchAllQuery.of(m -> m)._toQuery();
    } else {
      query = BoolQuery.of(b -> b
          .should(PrefixQuery.of(p -> p.field("title.autocomplete").value(keyword))._toQuery())
          .should(PrefixQuery.of(p -> p.field("title.ngram").value(keyword))._toQuery())
      )._toQuery();
    }

    SearchRequest request = SearchRequest.of(s -> s
        .index("meeting")
        .from(from)
        .size(size)
        .query(query)
    );

    try {
      SearchResponse<MeetingEsDocument> resp = client.search(request, MeetingEsDocument.class);
      List<MeetingEsDocument> content = resp.hits().hits().stream()
          .map(Hit::source)
          .collect(Collectors.toList());
      long total = resp.hits().total().value();
      return new PageImpl<>(content, PageRequest.of(page, size), total);

    } catch (IOException e) {
      log.error("Meeting 검색 오류", e);
      throw new RuntimeException("Meeting 검색 중 오류 발생", e);
    }
  }

  /** 자동완성: title.autocomplete 필드에서 MatchQuery 사용 (최대 10건) */
  public List<String> autocomplete(String keyword) {
    try {
      Query query = MatchQuery.of(m -> m.field("title.autocomplete").query(keyword))._toQuery();
      SearchRequest req = SearchRequest.of(s -> s
          .index("meeting")
          .query(query)
          .size(10)
      );
      SearchResponse<MeetingEsDocument> resp = client.search(req, MeetingEsDocument.class);
      return resp.hits().hits().stream()
          .map(Hit::source)
          .map(MeetingEsDocument::getTitle)
          .distinct()
          .collect(Collectors.toList());

    } catch (IOException e) {
      log.error("Meeting 자동완성 오류", e);
      throw new RuntimeException("Meeting 자동완성 검색 오류", e);
    }
  }
}
