package org.com.moodbook.batch.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BatchBookResponse {

	private Long bookId;
	private String isbn13;
	private String title;
	private String author;
	private String publisher;
	private String pubDate;
	private BigDecimal reputation;
	private String coverImage;
	private String description;
	private String categoryName;

	@Builder
	public BatchBookResponse(Long id, String title, String isbn13, String author, String publisher, String pubDate,
		BigDecimal reputation, String coverImage, String description, String categoryName) {
		this.bookId = id;
		this.title = title;
		this.isbn13 = isbn13;
		this.author = author;
		this.publisher = publisher;
		this.pubDate = pubDate;
		this.reputation = reputation;
		this.coverImage = coverImage;
		this.description = description;
		this.categoryName = categoryName;
	}
}
