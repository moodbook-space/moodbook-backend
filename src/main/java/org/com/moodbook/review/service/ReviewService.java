package org.com.moodbook.review.service;

import org.com.moodbook.review.dto.ReviewRequest;
import org.com.moodbook.review.dto.ReviewResponse;
import org.com.moodbook.review.dto.ReviewUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    /** 리뷰 조회 **/
    Page<ReviewResponse> getReviewsByBookId(Long id, Pageable pageable);

    /** 리뷰 추가 **/
    void createReview(Long bookId, Long memberId, ReviewRequest request);

    /** 리뷰 수정 **/
    void updateReview(Long reviewId, Long memberId, ReviewUpdateRequest updateRequest);

    /** 리뷰 삭제 **/
    void deleteReview(Long reviewId, Long memberId);

}
