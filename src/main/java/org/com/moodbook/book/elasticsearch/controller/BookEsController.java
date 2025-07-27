package org.com.moodbook.book.elasticsearch.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.com.moodbook.book.elasticsearch.service.BookEsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books/search")
public class BookEsController {

  private final BookEsService bookEsService;

  @GetMapping("/all")
  public ResponseEntity<Page<BookEsDocument>> getAllBooks(
      @PageableDefault(size = 10) Pageable pageable
  ) {
    Page<BookEsDocument> books = bookEsService.searchAllBooks(pageable);
    return ResponseEntity.ok(books);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<BookEsDocument>> searchBooks(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Page<BookEsDocument> result = bookEsService.search(keyword, page, size);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/autocomplete")
  public List<String> autocomplete(@RequestParam String keyword) {
    return bookEsService.autocomplete(keyword);
  }

}
