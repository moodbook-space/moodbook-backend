package org.com.moodbook.admin.book.service;

import java.util.List;
import org.com.moodbook.admin.book.dto.AdminBookDTO;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminBookService {

  Page<AdminBookDTO> searchBooks(String query, Pageable pageable);

  Page<AdminBookDTO> getBookList(Pageable pageable);

  void deleteById(Long bookId);

  List<BatchBookResponse> aladinBookSearch(String keyword);

  void addBook(BatchBookResponse batchBookResponse);
}

