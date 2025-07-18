package org.com.moodbook.admin.book.service.impl;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.admin.book.dto.AdminBookDTO;
import org.com.moodbook.admin.book.repository.AdminBookRepository;
import org.com.moodbook.admin.book.service.AdminBookService;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.com.moodbook.batch.job.BookApiReader;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminBookServiceImpl implements AdminBookService {

  private final AdminBookRepository adminBookRepository;
  private final BookApiReader bookApiReader;

  @Override
  @Transactional(readOnly = true)
  public Page<AdminBookDTO> getBookList(Pageable pageable) {
    return adminBookRepository.findAllPaging(pageable);
  }

  @Override
  public Page<AdminBookDTO> searchBooks(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      return adminBookRepository.findAllPaging(pageable); // 기존 전체 목록
    }
    return adminBookRepository.find(query, pageable);
  }

  @Override
  public void deleteById(Long bookId){
    if(!adminBookRepository.existsById(bookId)){
      throw new BaseException(ErrorCode.CHATROOM_NOT_FOUND);
    }
    adminBookRepository.deleteById(bookId);
  }

  @Override
  public List<BatchBookResponse> aladinBookSearch(String keyword) {
    Gson gson = new Gson();
    Set<String> set = new HashSet<>();

    return bookApiReader.fetchBooksByKeywordAndPage(keyword, 1, gson, set);
  }

  @Override
  public void addBook(BatchBookResponse batchBookResponse){
    Book book = Book.builder()
        .isbn13(batchBookResponse.getIsbn13())
        .title(batchBookResponse.getTitle())
        .author(batchBookResponse.getAuthor())
        .publisher(batchBookResponse.getPublisher())
        .pubDate(batchBookResponse.getPubDate())
        .reputation(batchBookResponse.getReputation())
        .coverImage(batchBookResponse.getCoverImage())
        .description(batchBookResponse.getDescription())
        .categoryName(batchBookResponse.getCategoryName())
        .build();

    BookCount bookCount = BookCount.builder()
        .viewCount(0L)
        .build();

    book.setBookCount(bookCount);

    adminBookRepository.save(book);
  };

}

