package org.com.moodbook.review.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.review.dto.ReviewRequest;
import org.com.moodbook.review.dto.ReviewResponse;
import org.com.moodbook.review.dto.ReviewUpdateRequest;
import org.com.moodbook.review.entity.Review;
import org.com.moodbook.review.repository.ReviewRepository;
import org.com.moodbook.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뷰 작성
     **/
    @Override
    @Transactional
    public void createReview(Long bookId, Long memberId, ReviewRequest request) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BaseException(ErrorCode.BOOK_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

        Review review = Review.builder()
            .book(book)
            .member(member)
            .content(request.getContent())
            .starRating(request.getStarRating())
            .build();

        reviewRepository.save(review);
    }

    /**
     * 리뷰 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByBookId(Long bookId, Pageable pageable) {
        if (bookId == null) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviews.map(ReviewResponse::fromEntity);
    }

    /**
     * 리뷰 수정
     **/
    @Override
    @Transactional
    public void updateReview(Long reviewId, Long memberId, ReviewUpdateRequest updateRequest) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getMember().getId().equals(memberId)) {
            throw new BaseException(ErrorCode.REVIEW_FORBIDDEN);
        }

        review.update(updateRequest.getContent(), updateRequest.getStarRating());
    }

    /**
     * 리뷰 삭제
     */
    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new BaseException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getMember().getId().equals(memberId)) {
            throw new BaseException(ErrorCode.REVIEW_FORBIDDEN);
        }

        reviewRepository.delete(review);
    }

}
