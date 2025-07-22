package org.com.moodbook.bookmark.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.bookmark.dto.BookmarkResponseDTO;
import org.com.moodbook.bookmark.entity.Bookmark;
import org.com.moodbook.bookmark.repository.BookmarkRepository;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final BookRepository bookRepository;
  private final MemberRepository memberRepository;

  // 유저에게 북마크 추가하기
  public BookmarkResponseDTO addBookmark(Long memberId, Long bookId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
        BaseException.MEMBER_NOT_FOUND);
    Book book = bookRepository.findById(bookId).orElseThrow(() ->
        BaseException.BOOK_NOT_FOUND);

    // 먼저 해당하는 북마크가 이미 존재하는지 확인해야 함!
    if (bookmarkRepository.existsByMember_IdAndBook_Id(memberId, bookId)) {
      throw BaseException.ALREADY_IN_BOOKMARK;
    }

    // 없다면, 추가한다
    Bookmark bookmark = Bookmark.of(member, book);

    bookmarkRepository.save(bookmark);

    return BookmarkResponseDTO.of(bookmark);
  }

  // 사용자의 모든 북마크 정보 반환
  @Override
  @Transactional(readOnly = true)
  public List<BookmarkResponseDTO> getBookmark(Long memberId) {
    //모든 북마크 정보 찾기, N+1 해결을 위한 코드 수정
    return bookmarkRepository.findAllBookInfoByMemberIdOrderByCreatedAtDesc(memberId);
  }

  // 특정 유저에게서 북마크 삭제
  public void deleteBookmark(Long memberId, Long bookId) {
    if (!bookmarkRepository.existsByMember_IdAndBook_Id(memberId, bookId)) {
      throw BaseException.BOOKMARK_NOT_FOUND;
    }

    bookmarkRepository.deleteByMember_IdAndBook_Id(memberId, bookId);
  }
}