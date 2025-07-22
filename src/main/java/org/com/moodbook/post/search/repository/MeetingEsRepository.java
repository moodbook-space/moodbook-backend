package org.com.moodbook.post.search.repository;

import org.com.moodbook.post.search.document.MeetingEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MeetingEsRepository extends ElasticsearchRepository<MeetingEsDocument, Long> {
}
