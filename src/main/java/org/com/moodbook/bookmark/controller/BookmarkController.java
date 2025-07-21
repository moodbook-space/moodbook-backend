package org.com.moodbook.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookmark.dto.BookmarkRequestDTO;
import org.com.moodbook.bookmark.dto.BookmarkResponseDTO;
import org.com.moodbook.bookmark.service.BookmarkServiceImpl;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
@Tag(name = "BookmarkController", description = "북마크 관련 기능을 사용하기 위한 컨트롤러")
public class BookmarkController {

  private final BookmarkServiceImpl bookmarkService;

  @Operation(summary = "북마크에 추가하기", description = "요청한 유저에게 bookId에 해당하는 책을 추가해준다")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크 추가에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "북마크 추가에 실패하였습니다.")
  })
  @PostMapping("")
  ResponseEntity<BookmarkResponseDTO> addBookmark(@RequestBody BookmarkRequestDTO request,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    BookmarkResponseDTO bookmarkResponseDTO = bookmarkService.addBookmark(
        customMemberDetails.getId(), request.getBookId());
    return ResponseEntity.status(HttpStatus.OK).body(bookmarkResponseDTO);
  }

  @Operation(summary = "북마크 목록 불러오기", description = "요청한 유저의 북마크 목록을 불러온다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크 목록을 조회하였습니다."),
      @ApiResponse(responseCode = "500", description = "북마크 목록 조회에 실패하였습니다.")
  })
  @GetMapping("")
  ResponseEntity<List<BookmarkResponseDTO>> getBookmark(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmark(customMemberDetails.getId());

    return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
  }

  @Operation(summary = "북마크에서 책 삭제하기", description = "북마크에서 Pathvariable에 지정된 책을 삭제한다. 성공 시 반환값은 없음!")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "북마크에서 책을 삭제하였습니다."),
      @ApiResponse(responseCode = "500", description = "북마크에서 책을 삭제하지 못했습니다.")
  })
  @DeleteMapping("/{bookId}")
  ResponseEntity<Void> deleteBookmark(@PathVariable Long bookId,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    bookmarkService.deleteBookmark(bookId, customMemberDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}