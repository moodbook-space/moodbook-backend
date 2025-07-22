package org.com.moodbook.book.elasticsearch.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.elasticsearch.dto.BookEsDocument;
import org.com.moodbook.book.elasticsearch.repository.BookEsRepository;
import org.com.moodbook.book.elasticsearch.service.BookEsService;
import org.com.moodbook.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookViewController {

  private final BookEsService bookEsService;
  private final BookEsRepository bookEsRepository;

  @GetMapping("/books")
  public String bookList(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Model model
  ) {
    Page<BookEsDocument> books = bookEsService.search(keyword, page, size);
    model.addAttribute("books", books);
    model.addAttribute("keyword", keyword);
    return "book/list";
  }


}

