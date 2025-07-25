package org.com.moodbook.post.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


  @Operation(
      summary = "독후감 페이징 검색",
      description = "부분문자열 검색과 자동완성을 결합한 키워드 기반 페이징 검색을 수행합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "검색 결과 반환에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "검색 중 오류가 발생하였습니다.")
  })
  @GetMapping("/search")
  public Page<ReportEsDocument> search(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return reportEsService.search(keyword, page, size);
  }

  @Operation(
      summary = "독후감 제목 자동완성",
      description = "입력된 키워드로 독후감 제목 자동완성 리스트를 최대 10건까지 반환합니다."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "자동완성 결과 반환에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "자동완성 중 오류가 발생하였습니다.")
  })
  @GetMapping("/autocomplete")
  public List<String> autocomplete(
      @RequestParam String keyword
  ) {
    return reportEsService.autocomplete(keyword);
  }
}
