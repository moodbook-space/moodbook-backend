package org.com.moodbook.batch.job;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.BookService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class BookItemWriter implements ItemWriter<Book> {

	private final BookRepository bookRepository;
	private final BookService bookService;

	@Override
	public void write(Chunk<? extends Book> chunk) throws Exception {
		List<String> isbn13List = chunk.getItems().stream()
			.map(Book::getIsbn13)
			.collect(Collectors.toList());

		Set<String> exists = new HashSet<>(bookRepository.findAllIsbn13In(isbn13List));

		List<Book> filteredBooks = chunk.getItems().stream()
				.filter(book -> !exists.contains(book.getIsbn13()))
				.collect(Collectors.toList());

		log.info("필터링 전: {}권, 중복 ISBN 제외 후: {}권", chunk.getItems().size(), filteredBooks.size());

		bookService.saveAllBooks(filteredBooks);
	}
}
