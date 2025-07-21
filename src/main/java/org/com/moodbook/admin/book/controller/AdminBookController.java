package org.com.moodbook.admin.book.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.admin.book.dto.AdminBookDTO;
import org.com.moodbook.admin.book.service.AdminBookService;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/book")
@RequiredArgsConstructor
public class AdminBookController {

  private final AdminBookService adminBookService;

  @GetMapping("")
  public Page<AdminBookDTO> getBookList(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    if (query != null && !query.isBlank()) {
      return adminBookService.searchBooks(query, pageable);
    }
    return adminBookService.getBookList(pageable);
  }

  @DeleteMapping("/{bookId}")
  public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
    adminBookService.deleteById(bookId);
    return ResponseEntity.ok("삭제 완료");

  }

  @GetMapping("/add")
  public ResponseEntity<List<BatchBookResponse>> addBook_search(
      @RequestParam("keyword") String keyword
//      @RequestParam("searchType") String searchType
  ) {
    List<BatchBookResponse> books = adminBookService.aladinBookSearch(keyword);

    return ResponseEntity.ok(books);
  }

  @PutMapping("/add")
  public ResponseEntity<String> addBook(
      @RequestBody BatchBookResponse batchBookResponse
  ) {
    adminBookService.addBook(batchBookResponse);
    return ResponseEntity.ok("추가 완료");
  }


}

