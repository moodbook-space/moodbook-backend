package org.com.moodbook.review.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.review.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("리뷰가 저장된 후 findByBookId를 호출하면 해당 도서의 리뷰 목록이 페이지 형태로 반환되어야 한다.")
    void findByBookIdSuccess() {
        // given
        Member member = Member.builder()
            .email("test@google.com")
            .contact("경기도 인천")
            .name("테스트유저")
            .password("1234")
            .build();
        memberRepository.save(member);

        Book book = Book.builder()
            .isbn13("1234567890123")
            .title("테스트 도서")
            .author("홍길동")
            .reputation(BigDecimal.valueOf(10))
            .categoryName("국내도서>어린이>초등5~6학년>자기계발")
            .coverImage("https://image.aladin.co.kr/product/29155/28/coversum/k512837377_3.jpg")
            .description("왜 부모님은 우리를 짜증나게 할까? 친구들이 괴롭혀서 힘들다면 어떻게 해야 할까? "
                + "자꾸 핸드폰만 들여다보는 내 심리는 뭘까? 분노와 불안의 감정을 어떻게 다스릴까? "
                + "등 우리 마음에 나타날 수 있는 다양한 문제들을 들여다보고, 어떤 깨달음이 나약해지고 힘들어진 마음에 도움이 될지 살펴본다.")
            .publisher("미래엔아이세움")
            .pubDate("2024-01-01")
            .build();
        bookRepository.save(book);

        Review review1 = Review.builder()
            .member(member)
            .book(book)
            .content("뭐 괜찮았지.")
            .starRating(4)
            .createdAt(LocalDateTime.now())
            .build();

        Review review2 = Review.builder()
            .member(member)
            .book(book)
            .content("또 읽고 싶어요.")
            .starRating(5)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // when
        Page<Review> result = reviewRepository.findByBookId(book.getId(),
            PageRequest.of(0, 10));

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("content")
            .contains("뭐 괜찮았지.", "또 읽고 싶어요.");
    }

    @Test
    @DisplayName("다수의 리뷰가 존재할 경우 페이지 요청(offset=1)도 정상적으로 동작해야 한다.")
    void findByBookIdPaginationWorksCorrectly() {
        // given
        Member member = memberRepository.save(Member.builder()
            .email("test@google.com")
            .contact("경기도 인천")
            .name("테스트유저")
            .password("1234")
            .build());
        Book book = bookRepository.save(Book.builder()
                .isbn13("1234567890123")
                .title("테스트 도서")
                .author("홍길동")
                .reputation(BigDecimal.valueOf(10))
                .categoryName("국내도서>어린이>초등5~6학년>자기계발")
                .coverImage("https://image.aladin.co.kr/product/29155/28/coversum/k512837377_3.jpg")
                .description("왜 부모님은 우리를 짜증나게 할까? 친구들이 괴롭혀서 힘들다면 어떻게 해야 할까? "
                    + "자꾸 핸드폰만 들여다보는 내 심리는 뭘까? 분노와 불안의 감정을 어떻게 다스릴까? "
                    + "등 우리 마음에 나타날 수 있는 다양한 문제들을 들여다보고, 어떤 깨달음이 나약해지고 힘들어진 마음에 도움이 될지 살펴본다.")
                .publisher("미래엔아이세움")
                .pubDate("2024-01-01")
                .build());

        for (int i = 0; i < 15; i++) {
            reviewRepository.save(Review.builder()
                .book(book)
                .member(member)
                .content("리뷰 " + i)
                .starRating(3)
                .createdAt(LocalDateTime.now().minusDays(i))
                .build());
        }

        // when
        Page<Review> firstPage = reviewRepository.findByBookId(book.getId(), PageRequest.of(0, 10));
        Page<Review> secondPage = reviewRepository.findByBookId(book.getId(), PageRequest.of(1, 10));

        // then
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(secondPage.getContent()).hasSize(5);
    }

}