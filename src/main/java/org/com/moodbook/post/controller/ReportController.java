package org.com.moodbook.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.util.PageableUtil;
import org.com.moodbook.post.dto.CreateReportRequest;
import org.com.moodbook.post.dto.ReportDetailResponse;
import org.com.moodbook.post.dto.ReportSummaryResponse;
import org.com.moodbook.post.dto.UpdateReportRequest;
import org.com.moodbook.post.service.ReportService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "ReportController", description = "독후감에 대한 컨트롤러")
@Validated
public class ReportController {

  private final ReportService reportService;

  /**
   * 독후감 작성
   */
  @PostMapping
  @Operation(summary = "독후감 작성",
      description = "독후감을 작성합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독후감 작성에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "독후감 작성에 실패하였습니다.")
  })
  public ResponseEntity<Long> createReport(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @Valid @RequestBody CreateReportRequest request
  ) {
    Long memberId = memberDetails.getId();
    Long reportId = reportService.createReport(memberId, request);
    return ResponseEntity.status(201).body(reportId);
  }

  /**
   * 단일 독후감 상세 조회
   */
  @GetMapping("/{id}")
  @Operation(summary = "독후감 상세 조회",
      description = "특정 독후감을 상세 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독후감 상세 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "독후감 상세 조회에 실패하였습니다.")
  })
  public ResponseEntity<ReportDetailResponse> getReport(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long id
  ) {
    ReportDetailResponse detail = reportService.getReport(md.getId(), id);
    return ResponseEntity.ok(detail);
  }

  /**
   * 독후감 목록 조회 (정렬 -> 최신순 좋아요순 조회수순)
   */
  @GetMapping
  @Operation(summary = "독후감 목록 조회",
      description = "독후감의 목록을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독후감 목록 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "독후감 목록 조회에 실패하였습니다.")
  })
  public ResponseEntity<Page<ReportSummaryResponse>> getReports(
      @AuthenticationPrincipal CustomMemberDetails md,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "latest") String sortType
  ) {
    Pageable pageable = PageableUtil.of(page, size, sortType);
    Page<ReportSummaryResponse> result = reportService.getReports(md.getId(), pageable);
    return ResponseEntity.ok(result);
  }

  /**
   * 독후감 수정
   */
  @PatchMapping("/{id}")
  @Operation(summary = "독후감 수정",
      description = "독후감을 수정합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독후감 수정에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "독후감 수정에 실패하였습니다.")
  })
  public ResponseEntity<Void> updateReport(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long id,
      @Valid @RequestBody UpdateReportRequest req
  ) {
    reportService.updateReport(md.getId(), id, req);
    return ResponseEntity.noContent().build();
  }

  /**
   * 독후감 삭제
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "독후감 삭제",
      description = "독후감을 삭제합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "독후감 삭제에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "독후감 삭제에 실패하였습니다.")
  })
  public ResponseEntity<Void> deleteReport(
      @AuthenticationPrincipal CustomMemberDetails md,
      @PathVariable("id") Long id
  ) {
    reportService.deleteReport(md.getId(), id);
    return ResponseEntity.noContent().build();
  }

  /**
   * 책 상세 페이지에서 사용하는 엔드포인트
   */
  @GetMapping("/books/{bookId}/reports")
  @Operation(summary = "책 상세 페이지에서 독후감 조회",
      description = "책 상세 페이지에서 독후감을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "책 상세 페이지에서 독후감 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "책 상세 페이지에서 독후감 조회에 실패하였습니다.")
  })
  public ResponseEntity<Page<ReportSummaryResponse>> getReportsByBook(
      @PathVariable Long bookId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    Page<ReportSummaryResponse> page = reportService.getReportsByBook(bookId, pageable);
    return ResponseEntity.ok(page);
  }

  /**
   * 내가 쓴 독후감을 조회하기 위한 엔드포인트 (마이페이지)
   */
  @GetMapping("/api/reports/my")
  @Operation(summary = "마이 페이지에서 독후감 조회",
      description = "마이 페이지에서 독후감을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "마이 페이지에서 독후감 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "마이 페이지에서 독후감 조회에 실패하였습니다.")
  })
  public ResponseEntity<Page<ReportSummaryResponse>> getMyReports(
      @AuthenticationPrincipal CustomMemberDetails md,
      Pageable pageable
  ) {
    return ResponseEntity.ok(reportService.getMyReports(md.getId(), pageable));
  }
}
