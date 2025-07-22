package org.com.moodbook.post.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.post.search.document.ReportEsDocument;
import org.com.moodbook.post.search.repository.ReportEsRepository;
import org.com.moodbook.post.repository.ReportRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportEsService {

  private final ReportEsRepository reportEsRepository;
  private final ReportRepository reportRepository;
  private final ElasticsearchClient client;

  @Transactional(readOnly = true)
  @PostConstruct
  public void initIndex() {
    if (reportEsRepository.count() == 0) {
      indexAllReports();
    }
  }

  @Transactional
  public void indexAllReports() {
    List<ReportEsDocument> docs = reportRepository.findAll().stream()
        .map(ReportEsDocument::fromEntity)
        .collect(Collectors.toList());
    reportEsRepository.saveAll(docs);
  }

  public Page<ReportEsDocument> searchAllReports(Pageable pageable) {
    return reportEsRepository.findAll(pageable);
  }

  public Page<ReportEsDocument> search(String keyword, int page, int size) {
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
        .index("report")
        .from(from)
        .size(size)
        .query(query)
    );

    try {
      SearchResponse<ReportEsDocument> resp = client.search(request, ReportEsDocument.class);
      List<ReportEsDocument> content = resp.hits().hits().stream()
          .map(Hit::source)
          .collect(Collectors.toList());
      long total = resp.hits().total().value();
      return new PageImpl<>(content, PageRequest.of(page, size), total);

    } catch (IOException e) {
      log.error("Report 검색 오류", e);
      throw new RuntimeException("Report 검색 중 오류 발생", e);
    }
  }

  public List<String> autocomplete(String keyword) {
    try {
      Query query = MatchQuery.of(m -> m.field("title.autocomplete").query(keyword))._toQuery();
      SearchRequest req = SearchRequest.of(s -> s
          .index("report")
          .query(query)
          .size(10)
      );
      SearchResponse<ReportEsDocument> resp = client.search(req, ReportEsDocument.class);
      return resp.hits().hits().stream()
          .map(Hit::source)
          .map(ReportEsDocument::getTitle)
          .distinct()
          .collect(Collectors.toList());

    } catch (IOException e) {
      log.error("Report 자동완성 오류", e);
      throw new RuntimeException("Report 자동완성 검색 오류", e);
    }
  }
}
