package org.com.moodbook.post.service;

import org.com.moodbook.post.dto.CreateReportRequest;
import org.com.moodbook.post.dto.ReportDetailResponse;
import org.com.moodbook.post.dto.ReportSummaryResponse;
import org.com.moodbook.post.dto.UpdateReportRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {

  // 새로운 독후감 작성
  Long createReport(Long memberId, CreateReportRequest request);

  // 단일 독후감 조회
  ReportDetailResponse getReport(Long reportId);

  // 독후감 목록 조회
  Page<ReportSummaryResponse> getReports(Pageable pageable);

  // 독후감 수정
  void updateReport(Long memberId, Long reportId, UpdateReportRequest request);

  // 독후감 삭제
  void deleteReport(Long memberId, Long reportId);

  // 특정 책에 달린 독후감 목록 조회 (도서 상세페이지에서 해당 도서의 독후감 목록조회를 위한 메서드)
  Page<ReportSummaryResponse> getReportsByBook(Long bookId, Pageable pageable);

  // 내가 쓴 독후감 목록 조회
  Page<ReportSummaryResponse> getMyReports(Long memberId, Pageable pageable);
}