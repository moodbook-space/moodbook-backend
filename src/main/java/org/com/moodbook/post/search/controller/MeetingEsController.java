package org.com.moodbook.post.search.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.search.document.MeetingEsDocument;
import org.com.moodbook.post.search.service.MeetingEsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingEsController {

  private final MeetingEsService meetingEsService;

  /**
   * 페이징 검색 (부분문자열 + 자동완성 함께 동작)
   * GET /api/meetings/search?keyword=abc&page=0&size=10
   */
  @GetMapping("/search")
  public Page<MeetingEsDocument> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return meetingEsService.search(keyword, page, size);
  }

  /**
   * 자동완성용 엔드포인트
   * GET /api/meetings/autocomplete?keyword=a
   */
  @GetMapping("/autocomplete")
  public List<String> autocomplete(
      @RequestParam String keyword
  ) {
    return meetingEsService.autocomplete(keyword);
  }
}
