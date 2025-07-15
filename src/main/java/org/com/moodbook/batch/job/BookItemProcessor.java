package org.com.moodbook.batch.job;

import org.com.moodbook.batch.dto.BatchBookResponse;
import org.com.moodbook.book.entity.Book;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BookItemProcessor implements ItemProcessor<BatchBookResponse, Book> {

	@Override
	public Book process(BatchBookResponse dto) {
		return Book.builder()
			.isbn13(dto.getIsbn13())
			.title(dto.getTitle())
			.author(dto.getAuthor())
			.publisher(dto.getPublisher())
			.pubDate(dto.getPubDate())
			.reputation(dto.getReputation())
			.coverImage(dto.getCoverImage())
			.description(dto.getDescription())
			.categoryName(dto.getCategoryName())
			.build();
	}
}
