package org.com.moodbook.bookmark.service;

import java.util.List;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.bookmark.dto.BookmarkResponseDTO;

public interface BookmarkService {
  // 특정 유저에게 북마크 추가하기
  BookmarkResponseDTO addBookmark(Long memberId, Long bookId);

  // 특정 유저의 북마크 로드하기
  List<BookmarkResponseDTO> getBookmark(Long memberId);

  // 특정 유저의 특정 북마크 삭제하기
  void deleteBookmark(Long memberId, Long bookId);
}
