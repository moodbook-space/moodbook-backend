package org.com.moodbook.book.elasticsearch.repository;

import org.com.moodbook.book.elasticsearch.dto.BookEsAutoCompleteDocument;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookEsAutoCompleteRepository extends
    ElasticsearchRepository<BookEsAutoCompleteDocument, Long> {

}
