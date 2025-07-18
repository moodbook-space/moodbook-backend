package org.com.moodbook.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.review.dto.ReviewRequest;
import org.com.moodbook.review.dto.ReviewResponse;
import org.com.moodbook.review.dto.ReviewUpdateRequest;
import org.com.moodbook.review.service.ReviewService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "ReviewController", description = "책 상세 페이지의 리뷰 컨트롤러")
public class ReviewController {

    private final ReviewService reviewService;

    /** 리뷰 생성 **/
    @PostMapping("/books/{bookId}/reviews")
    @Operation(summary = "리뷰 생성", description = "리뷰를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "리뷰 추가에 성공하였습니다."),
        @ApiResponse(responseCode = "500", description = "리뷰 추가에 실패하였습니다.")
    })
    public ResponseEntity<Void> createReview(
        @PathVariable Long bookId,
        @AuthenticationPrincipal CustomMemberDetails memberDetails,
        @RequestBody @Valid ReviewRequest request) {
        reviewService.createReview(bookId, memberDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 리뷰 조회 **/
    @GetMapping("/books/{bookId}/reviews")
    @Operation(summary = "리뷰 조회", description = "특정 책의 리뷰를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "특정 책의 리뷰 조회에 성공하였습니다."),
        @ApiResponse(responseCode = "500", description = "특정 책의 리뷰 조회에 실패하였습니다.")
    })
    public ResponseEntity<Page<ReviewResponse>> getReviews(
        @PathVariable Long bookId,
        @PageableDefault(size = 30, page = 0, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId, pageable));
    }

    /** 리뷰 수정 **/
    @PatchMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "특정 책의 리뷰를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "리뷰 수정에 성공하였습니다."),
        @ApiResponse(responseCode = "500", description = "리뷰 수정에 실패하였습니다.")
    })
    public ResponseEntity<Void> updateReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomMemberDetails memberDetails,
        @RequestBody @Valid ReviewUpdateRequest updateRequest) {
        reviewService.updateReview(reviewId, memberDetails.getId(), updateRequest);
        return ResponseEntity.ok().build();
    }

    /** 리뷰 삭제 **/
    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "특정 책의 리뷰를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "리뷰 삭제에 성공하였습니다."),
        @ApiResponse(responseCode = "500", description = "리뷰 삭제에 실패하였습니다.")
    })
    public ResponseEntity<Void> deleteReview(
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomMemberDetails memberDetails) {
        reviewService.deleteReview(reviewId, memberDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
