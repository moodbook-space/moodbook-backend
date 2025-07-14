package org.com.moodbook.post.service;

import org.com.moodbook.post.dto.CreateReportRequest;
import org.com.moodbook.post.dto.ReportDetailResponse;
import org.com.moodbook.post.dto.ReportSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
  // 새로운 독후감 작성
  Long createReport(Long memberId, CreateReportRequest request);

  // 단일 독후감 조회
  ReportDetailResponse getReport(Long reportId);

  // 독후감 목록 조회
  Page<ReportSummaryResponse> getReports(Pageable pageable);
}