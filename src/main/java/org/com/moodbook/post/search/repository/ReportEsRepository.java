package org.com.moodbook.post.search.repository;

import org.com.moodbook.post.search.document.ReportEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReportEsRepository extends ElasticsearchRepository<ReportEsDocument, Long> {
}
