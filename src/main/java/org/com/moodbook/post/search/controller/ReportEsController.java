package org.com.moodbook.post.search.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.search.document.ReportEsDocument;
import org.com.moodbook.post.search.service.ReportEsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportEsController {

  private final ReportEsService reportEsService;

  /**
   * 페이징 검색 (부분문자열 + 자동완성 함께 동작)
   * GET /api/reports/search?keyword=xyz&page=0&size=10
   */
  @GetMapping("/search")
  public Page<ReportEsDocument> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return reportEsService.search(keyword, page, size);
  }

  /**
   * 자동완성용 엔드포인트
   * GET /api/reports/autocomplete?keyword=x
   */
  @GetMapping("/autocomplete")
  public List<String> autocomplete(
      @RequestParam String keyword
  ) {
    return reportEsService.autocomplete(keyword);
  }
}
