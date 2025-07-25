package org.com.moodbook.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.BookEmotionRecommendAllRequest;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/books")
@Tag(name = "BookController", description = "도서 관련 API")
@RestController
@Slf4j
public class BookController {

  private final BookService bookService;

  @GetMapping(value = "/recommendations/star")
  @Operation(summary = "알라딘 평점순 - 높은 순으로", description = "알라딘에서 추출한 데이터를 가지고 평점 기준으로 반환")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "도서 리스트 반환 성공"),
      @ApiResponse(responseCode = "500", description = "도서 리스트 반환 오류")})
  public ResponseEntity<Page<BookResponse>> getRecommendedBooks(
      @PageableDefault(size = 10, page = 0) Pageable pageable,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;
    Page<BookResponse> recommendedBooks = bookService.getRecommendedBooks(pageable, memberId);
    return ResponseEntity.status(HttpStatus.OK).body(recommendedBooks);
  }

  @Operation(summary = "전체 도서 조회", description = "출판일 기준으로 전체 도서를 조회합니다.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "전체 도서 리스트 반환 성공"),
      @ApiResponse(responseCode = "500", description = "전체 도서 리스트 반환 오류")})
  @GetMapping("")
  public ResponseEntity<Page<BookResponse>> getBooks(
      @PageableDefault(size = 100, page = 0) Pageable pageable,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;
    Page<BookResponse> books = bookService.getAllBooks(pageable, memberId);
    return ResponseEntity.status(HttpStatus.OK).body(books);
  }

  @GetMapping("/{bookId}")
  @Operation(summary = "도서 상세 조회", description = "특정 도서를 상세 조회합니다.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "도서 상세 정보 조회 성공"),
      @ApiResponse(responseCode = "500", description = "도서 상세 정보 조회 실패")})
  public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long bookId,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;
    BookResponse book = bookService.getBookById(bookId, memberId);
    return ResponseEntity.status(HttpStatus.OK).body(book);
  }

  @GetMapping("/trending")
  @Operation(summary = "조회수별 도서 조회", description = "조회수별로 도서 조회를 합니다.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "조회수별 도서 조회 성공"),
      @ApiResponse(responseCode = "500", description = "조회수별 도서 조회 실패")})
  public ResponseEntity<Page<BookResponse>> getTrendingBooks(
      @PageableDefault(size = 20, page = 0) Pageable pageable,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;
    Page<BookResponse> trendingBooks = bookService.getTrendingBooks(pageable, memberId);
    return ResponseEntity.status(HttpStatus.OK).body(trendingBooks);
  }

  @GetMapping("/recommend/emotion/top10")
  @Operation(summary = "감정별 인기 추천 도서 Top10", description = "감정별 인기 추천 도서 조회를 합니다.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "인기 추천 도서 정보 조회 성공"),
      @ApiResponse(responseCode = "500", description = "인기 추천 도서 정보 조회 실패")})
  public ResponseEntity<List<BookEmotionRecommendResponse>> getBooksByEmotionTop10(
      @RequestParam String emotion, @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;
    List<BookEmotionRecommendResponse> result = bookService.getBooksByEmotionTop10(emotion,
        memberId);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/recommend/emotion/all")
  @Operation(summary = "모든 감정별 추천 도서", description = "감정별 추천 도서를 모두 조회합니다.")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "감정별 추천 도서 모두 조회 성공"),
      @ApiResponse(responseCode = "500", description = "감정별 추천 도서 모두 조회 실패")})
  public ResponseEntity<List<BookEmotionRecommendAllResponse>> getBooksByEmotionAll(
      @RequestParam String emotion, @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Long memberId = (memberDetails != null) ? memberDetails.getId() : null;

    BookEmotionRecommendAllRequest request = BookEmotionRecommendAllRequest.builder()
        .emotionTag(emotion).page(page).size(size).build();

    List<BookEmotionRecommendAllResponse> result = bookService.getBooksByEmotionDesc(request,
        memberId);
    return ResponseEntity.ok(result);
  }


    @Operation(summary = "(관리자용) 책 전체 조회",
        description = "(관리자용) db에 있는 모든 책 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "(관리자용) 도서 리스트 반환 성공"),
        @ApiResponse(responseCode = "500", description = "(관리자용) 도서 리스트 반환 오류")
    })
    @GetMapping("/admin")
    public Page<BookResponse> getDbBookList(
        @RequestParam(defaultValue = "") String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return bookService.getDbBookList(query, pageable);

    }

    @Operation(summary = "(관리자용) 책 제거",
        description = "(관리자용) db에 있는 책 제거")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "(관리자용) 책 제거 성공"),
        @ApiResponse(responseCode = "500", description = "(관리자용) 책 제거 오류")
    })
    @DeleteMapping("/admin/{bookId}")
    public ResponseEntity<String> deleteBookById(@PathVariable Long bookId) {
        bookService.deleteBookById(bookId);
        return ResponseEntity.ok("삭제 완료");

    }


    @Operation(summary = "(관리자용) 알라딘 api 책 검색",
        description = "(관리자용) 알라딘 api로 책 검색")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "(관리자용) 알라딘 api 책 검색 성공"),
        @ApiResponse(responseCode = "500", description = "(관리자용) 알라딘 api 책 검색 오류")
    })
    @GetMapping("/admin/add")
    public ResponseEntity<List<BookResponse>> searchForNewBook(
        @RequestParam("keyword") String keyword
    ) {
        List<BookResponse> books = bookService.searchForNewBook(keyword);

        return ResponseEntity.ok(books);
    }


    @Operation(summary = "(관리자용) 검색된 책 db에 추가 (한 개)",
        description = "(관리자용) 검색된 책 하나를 선택해 db에 추가합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "(관리자용) db에 책 책 추가 성공"),
        @ApiResponse(responseCode = "500", description = "(관리자용) db에 책 추가 오류")
    })
    @PostMapping("/admin/add")
    public ResponseEntity<String> addBook(@RequestBody BookResponse bookResponse) {
        boolean success = bookService.addBook(bookResponse);
        if (success) {
            return ResponseEntity.ok("등록 완료");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 책입니다.");
        }
    }


}
