package org.com.moodbook.recentbookviews.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.recentbookviews.dto.ApiResponse;
import org.com.moodbook.recentbookviews.dto.RecentBookViewResponse;
import org.com.moodbook.recentbookviews.service.RecentBookViewService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recent-books")
@Tag(name = "RecentBookViewController", description = "최근에 본 도서 조회")
public class RecentBookViewController {

    private final RecentBookViewService recentBookViewService;

    @GetMapping("")
    @Operation(summary = "최근에 본 도서 조회")
    public ResponseEntity<ApiResponse<Page<RecentBookViewResponse>>> getRecentBooks(
        @AuthenticationPrincipal CustomMemberDetails memberDetails,
        @PageableDefault(size = 20, sort = "viewedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long memberId = memberDetails.getId();
        Page<RecentBookViewResponse> recentBooks =
            recentBookViewService.getRecentBookViews(memberId, pageable);
        String message = recentBooks.isEmpty() ? "조회한 책이 없습니다." : "최근 조회한 책 목록입니다.";

        return ResponseEntity.ok(
            ApiResponse.<Page<RecentBookViewResponse>>builder()
                .message(message)
                .data(recentBooks)
                .build()
        );
    }

}
