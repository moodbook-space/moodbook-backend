package org.com.moodbook.book.elasticsearch.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.elasticsearch.service.BookEsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookEsController {

  private final BookEsService bookEsService;

  @GetMapping("/api/books/autocomplete")
  public List<String> autocomplete(@RequestParam String keyword) {
    return bookEsService.autocomplete(keyword);
  }

}
