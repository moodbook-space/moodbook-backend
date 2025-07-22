package org.com.moodbook.book.elasticsearch.repository;

import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookEsRepository extends ElasticsearchRepository<BookEsDocument, Long> {


}
