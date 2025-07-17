package org.com.moodbook.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookmark.dto.BookmarkRequestDTO;
import org.com.moodbook.bookmark.dto.BookmarkResponseDTO;
import org.com.moodbook.bookmark.service.BookmarkServiceImpl;
import org.com.moodbook.security.core.CustomMemberDetails;
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

  @PostMapping("")
  @Operation(summary = "북마크에 추가하기", description = "요청한 유저에게 bookId에 해당하는 책을 추가해준다")
  ResponseEntity<?> addBookmark(@RequestBody BookmarkRequestDTO request,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    BookmarkResponseDTO bookmarkResponseDTO = bookmarkService.addBookmark(
        customMemberDetails.getId(), request.getBookId());
    return ResponseEntity.ok(bookmarkResponseDTO);
  }

  @GetMapping("")
  @Operation(summary = "북마크 목록 불러오기", description = "요청한 유저의 북마크 목록을 불러온다.")
  ResponseEntity<?> getBookmark(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmark(customMemberDetails.getId());
    if (bookmarks.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(bookmarks);
  }

  @DeleteMapping("/{bookId}")
  @Operation(summary = "북마크에서 책 삭제하기", description = "북마크에서 Pathvariable에 지정된 책을 삭제한다.")
  ResponseEntity<?> deleteBookmark(@PathVariable Long bookId,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
    bookmarkService.deleteBookmark(bookId, customMemberDetails.getId());
    return ResponseEntity.ok().build();
  }


}