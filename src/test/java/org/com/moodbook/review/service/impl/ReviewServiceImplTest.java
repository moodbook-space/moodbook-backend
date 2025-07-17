package org.com.moodbook.review.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)             // JUnit5에서 Mockito를 활성화하기 위한 설정
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @DisplayName("리뷰 작성 시 해당 도서와 회원이 존재하면 리뷰가 정상적으로 저장된다.")
    @Test
    public void createReviewSuccess() {
        // given
        Long bookId = 1L;
        Long memberId = 2L;
        ReviewRequest request = new ReviewRequest("좋은 책이에요.", 4);

        // 가짜 도서, 회원 데이터 준비
        Book book = Book.builder()
            .id(bookId)
            .build();

        Member member = Member.builder()
            .id(memberId)
            .build();

        // Mock 리포지토리 동작 정의: 도서, 회원이 존재하는 경우
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // when
        reviewService.createReview(bookId, memberId, request);

        // then
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        // save() 호출 검증 및 Review 객체 캡처
        verify(reviewRepository).save(captor.capture());

        Review savedReview = captor.getValue();
        assertThat(savedReview.getContent()).isEqualTo("좋은 책이에요.");
        assertThat(savedReview.getStarRating()).isEqualTo(4);
        assertThat(savedReview.getBook()).isEqualTo(book);
        assertThat(savedReview.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("리뷰 작성 시 도서가 존재하지 않으면 BOOK_NOT_FOUND 예외가 발생한다.")
    void createReviewBookNotFound() {
        // given
        Long bookId = 99L;
        Long memberId = 1L;
        ReviewRequest request = new ReviewRequest("내용", 3);

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(bookId, memberId, request))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.BOOK_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("특정 도서에 대한 리뷰 목록을 정상적으로 조회하면 리뷰 정보"
        + "(작성자 이름, 내용, 평점, 작성일)가 포함된다.")
    void getReviewsByBookIdSuccess() {
        // given
        Long bookId = 1L;
        PageRequest pageable = PageRequest.of(0, 10);

        // 리뷰 작성자 정보
        Member reviewer = Member.builder()
            .id(2L)
            .name("홍길동")
            .build();

        // 실제 저장된 리뷰 하나
        Review review = Review.builder()
            .id(101L)
            .member(reviewer)
            .content("재미있는 책이었어요.")
            .starRating(5)
            .createdAt(LocalDateTime.of(2024, 1, 10, 15, 30))
            .build();

        when(reviewRepository.findByBookId(bookId, pageable))
            .thenReturn(new PageImpl<>(List.of(review)));

        // when
        Page<ReviewResponse> result = reviewService.getReviewsByBookId(bookId, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        ReviewResponse response = result.getContent().get(0);
        assertThat(response.getReviewId()).isEqualTo(101L);
        assertThat(response.getReviewerName()).isEqualTo("홍길동");
        assertThat(response.getContent()).isEqualTo("재미있는 책이었어요.");
        assertThat(response.getStarRating()).isEqualTo(5);
        assertThat(response.getCreatedAt()).isEqualTo(
            LocalDateTime.of(2024, 1, 10, 15, 30));
    }

    @Test
    @DisplayName("리뷰 작성자가 본인의 리뷰 내용을 수정하면 내용과 별점이 정상적으로 변경된다.")
    void updateReviewSuccess() {
        // given
        Long reviewId = 1L;
        Long memberId = 2L;

        Member reviewer = Member.builder()
            .id(memberId)
            .name("작성자")
            .build();

        Review review = Review.builder()
            .id(reviewId)
            .member(reviewer)
            .content("이전 내용")
            .starRating(3)
            .build();

        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest("수정된 리뷰 내용", 4);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewService.updateReview(reviewId, memberId, updateRequest);

        // then
        assertThat(review.getContent()).isEqualTo("수정된 리뷰 내용");
        assertThat(review.getStarRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("리뷰 수정 시 작성자가 아닌 사용자가 수정 요청하면 REVIEW_FORBIDDEN 예외가 발생한다.")
    void updateReviewForbidden() {
        // given
        Long reviewId = 1L;
        Long memberId = 2L;     // 요청한 사용자
        Long authorId = 1L;     // 실제 리뷰 작성자

        Member author = Member.builder()
            .id(authorId)
            .name("작성자")
            .build();

        Review review = Review.builder()
            .id(reviewId)
            .content("기존 내용")
            .starRating(3)
            .member(author)
            .build();

        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest("수정된 내용", 5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when & then
        assertThatThrownBy(() -> reviewService.updateReview(reviewId, memberId, updateRequest))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.REVIEW_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("리뷰 작성자 본인이면 리뷰를 삭제할 수 있다.")
    void deleteReviewSuccess() {
        // given
        Long reviewId = 1L;
        Long memberId = 2L;
        Review review = Review.builder().id(reviewId).member(Member.builder().id(memberId).build()).build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        reviewService.deleteReview(reviewId, memberId);

        // then
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("리뷰 삭제 시 작성자가 아니면 REVIEW_FORBIDDEN 예외가 발생한다.")
    void deleteReviewForbidden() {
        // given
        Long reviewId = 1L;
        Long memberId = 2L;                 // 요청한 사용자
        Review review = Review.builder()
            .id(reviewId)
            .member(Member.builder()        // 작성자는 다른 사람
                .id(1L)
                .build()).build();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewId, memberId))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.REVIEW_FORBIDDEN.getMessage());
    }


}