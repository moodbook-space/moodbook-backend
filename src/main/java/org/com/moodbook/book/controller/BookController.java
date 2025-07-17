package org.com.moodbook.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.BookEmotionRecommendAllRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/books")
@RestController
@Slf4j
public class BookController {

    private final BookService bookService;

    /** Recommendation (기준: 알라딘 평점순 - 높은 순으로) **/
    /** Recommendation (기준: 알라딘 평점순 - 높은 순으로) **/
    @GetMapping(value = "/recommendations/star")
    @Operation(summary = "알라딘 평점순 - 높은 순으로",
        description = "알라딘에서 추출한 데이터를 가지고 평점 기준으로 반환")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "도서 리스트 반환 성공"),
        @ApiResponse(responseCode = "500", description = "도서 리스트 반환 오류")
    })
    public ResponseEntity<Page<BookResponse>> getRecommendedBooks(
        @PageableDefault(size = 10, page = 0)
        Pageable pageable) {
        Page<BookResponse> recommendedBooks = bookService.getRecommendedBooks(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(recommendedBooks);
    }

    /** 도서 상세 조회 **/
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookDetail(
        @PathVariable Long bookId, @AuthenticationPrincipal CustomMemberDetails memberDetails) {
        Long memberId = memberDetails.getId();
        BookResponse book = bookService.getBookById(bookId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    /** Trending Now (기준: 책 조회수) **/
    @GetMapping("/trending")
    public ResponseEntity<Page<BookResponse>> getTrendingBooks(
        @PageableDefault(size = 20, page = 0)
        Pageable pageable) {
        Page<BookResponse> trendingBooks = bookService.getTrendingBooks(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(trendingBooks);
    }

    @PostMapping("/recommend/emotion/top10")
    public ResponseEntity<List<BookEmotionRecommendResponse>> getBooksByEmotionTop10(
        @RequestBody @Valid BookEmotionRecommendRequest request
    ) {
        List<BookEmotionRecommendResponse> result = bookService.getBooksByEmotionTop10(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/recommend/emotion/all")
    public ResponseEntity<List<BookEmotionRecommendAllResponse>> getBooksByEmotionAll(
        @RequestBody @Valid BookEmotionRecommendAllRequest request
    ) {
        List<BookEmotionRecommendAllResponse> result = bookService.getBooksByEmotionDesc(request);
        return ResponseEntity.ok(result);
    }
}
