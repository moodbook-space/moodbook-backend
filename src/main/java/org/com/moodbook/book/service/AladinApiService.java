package org.com.moodbook.book.service;

import org.com.moodbook.book.dto.AladinBookResponse;

public interface AladinApiService {

  AladinBookResponse fetchBookByIsbn13(String isbn13);

}
