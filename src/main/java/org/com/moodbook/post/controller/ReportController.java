package org.com.moodbook.post.controller;

import jakarta.validation.Valid;
import org.com.moodbook.post.dto.CreateReportRequest;
import org.com.moodbook.post.dto.ReportDetailResponse;
import org.com.moodbook.post.dto.ReportSummaryResponse;
import org.com.moodbook.post.service.ReportService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@Validated
public class ReportController {

  private final ReportService reportService;

  @Autowired
  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  /** 독후감 작성 */
  @PostMapping
  public ResponseEntity<Long> createReport(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @Valid @RequestBody CreateReportRequest request
  ) {
    Long memberId = memberDetails.getId();
    Long reportId = reportService.createReport(memberId, request);
    return ResponseEntity.status(201).body(reportId);
  }

  /** 단일 독후감 상세 조회 */
  @GetMapping("/{id}")
  public ResponseEntity<ReportDetailResponse> getReport(@PathVariable("id") Long id) {
    ReportDetailResponse detail = reportService.getReport(id);
    return ResponseEntity.ok(detail);
  }

  /** 독후감 목록 조회 */
  @GetMapping
  public ResponseEntity<Page<ReportSummaryResponse>> getReports(Pageable pageable) {
    Page<ReportSummaryResponse> page = reportService.getReports(pageable);
    return ResponseEntity.ok(page);
  }
}
