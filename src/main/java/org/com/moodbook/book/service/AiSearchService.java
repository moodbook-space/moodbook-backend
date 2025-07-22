package org.com.moodbook.book.service;

import org.com.moodbook.book.dto.AIResponse;
import org.com.moodbook.book.dto.BookResponse;

public interface AiSearchService {

  AIResponse askQuestion(String prompt);

  BookResponse fetchBookByIsbn13(String isbn13);

}
