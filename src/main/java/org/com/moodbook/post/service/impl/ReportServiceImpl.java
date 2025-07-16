package org.com.moodbook.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.dto.CreateReportRequest;
import org.com.moodbook.post.dto.ReportDetailResponse;
import org.com.moodbook.post.dto.ReportSummaryResponse;
import org.com.moodbook.post.dto.UpdateReportRequest;
import org.com.moodbook.post.entity.Report;
import org.com.moodbook.post.entity.MoodTag;
import org.com.moodbook.post.repository.MoodTagRepository;
import org.com.moodbook.post.repository.ReportRepository;
import org.com.moodbook.post.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

  private final ReportRepository reportRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;
  private final MoodTagRepository tagRepository;

  @Override
  public Long createReport(Long memberId, CreateReportRequest req) {
    // 작성자 검증
    Member author = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    // 책 정보 검증
    var book = bookRepository.findById(req.getBookId())
        .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));

    // 태그 조회 (최소 하나 이상 필요하다면 추가 검증)
    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    // 4) 엔티티 생성 및 저장
    Report report = Report.builder()
        .title(req.getTitle())
        .content(req.getContent())
        .member(author)
        .book(book)
        .moodTags(tags)
        .build();

    reportRepository.save(report);
    return report.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public ReportDetailResponse getReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new BaseException(ErrorCode.REPORT_NOT_FOUND));

    return ReportDetailResponse.builder()
        .id(report.getId())
        .title(report.getTitle())
        .content(report.getContent())
        .bookId(report.getBook().getId())
        .bookTitle(report.getBook().getTitle())
        .bookAuthor(report.getBook().getAuthor())
        .tags(report.getMoodTags().stream()
            .map(MoodTag::getName)
            .collect(Collectors.toList()))
        .authorName(report.getMember().getName())
        .createdAt(report.getCreatedAt())
        .updatedAt(report.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ReportSummaryResponse> getReports(Pageable pageable) {
    return reportRepository.findAll(pageable)
        .map(r -> ReportSummaryResponse.builder()
            .id(r.getId())
            .title(r.getTitle())
            .authorName(r.getMember().getName())
            .createdAt(r.getCreatedAt())
            .viewCount(r.getViewCount())
            .likeCount(r.getLikeCount())
            .tags(r.getMoodTags().stream()
                .map(MoodTag::getName)
                .collect(Collectors.toList()))
            .build()
        );
  }

  @Override
  public void updateReport(Long memberId, Long reportId, UpdateReportRequest req) {
    Report rpt = reportRepository.findById(reportId)
        .orElseThrow(() -> new BaseException(ErrorCode.REPORT_NOT_FOUND));
    if (!rpt.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }

    // book 교체
    Book book = bookRepository.findById(req.getBookId())
        .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));
    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    rpt.setTitle(req.getTitle());
    rpt.setContent(req.getContent());
    rpt.setBook(book);
    rpt.setMoodTags(tags);

    reportRepository.save(rpt);
  }

  @Override
  public void deleteReport(Long memberId, Long reportId) {
    Report rpt = reportRepository.findById(reportId)
        .orElseThrow(() -> new BaseException(ErrorCode.REPORT_NOT_FOUND));
    if (!rpt.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }
    reportRepository.deleteById(reportId);  // 소프트 삭제
  }

  // 도서 상세페이지에서 해당 도서에 관한 독후감목록만조회하기위한 메서드
  @Override
  @Transactional(readOnly = true)
  public Page<ReportSummaryResponse> getReportsByBook(Long bookId, Pageable pageable) {
    return reportRepository.findByBook_Id(bookId, pageable)
        .map(r -> ReportSummaryResponse.builder()
            .id(r.getId())
            .title(r.getTitle())
            .authorName(r.getMember().getName())
            .createdAt(r.getCreatedAt())
            .viewCount(r.getViewCount())
            .likeCount(r.getLikeCount())
            .tags(r.getMoodTags().stream()
                .map(MoodTag::getName)
                .collect(Collectors.toList()))
            .build()
        );
  }

  // 내가 쓴 독후감들 전체 조회 (마이페이지에서 가능)
  @Override
  @Transactional(readOnly = true)
  public Page<ReportSummaryResponse> getMyReports(Long memberId, Pageable pageable) {
    return reportRepository.findByMember_Id(memberId, pageable)
        .map(r -> ReportSummaryResponse.builder()
            .id(r.getId())
            .title(r.getTitle())
            .authorName(r.getMember().getName())
            .createdAt(r.getCreatedAt())
            .viewCount(r.getViewCount())
            .likeCount(r.getLikeCount())
            .tags(r.getMoodTags().stream().map(MoodTag::getName).toList())
            .build()
        );
  }
}
