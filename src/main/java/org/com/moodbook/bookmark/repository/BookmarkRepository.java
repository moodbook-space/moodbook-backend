package org.com.moodbook.bookmark.repository;

import java.util.List;
import org.com.moodbook.bookmark.dto.BookmarkResponseDTO;
import org.com.moodbook.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  // 멤버ID, 책 ID기반으로 이미 북마크에 등록된 정보인지 불러오기
  Boolean existsByMember_IdAndBook_Id(Long memberId, Long bookId);

  // 유저번호를 받아 북마크에 등록된 모든 정보 가져오기
  void deleteByMember_IdAndBook_Id(Long memberId, Long bookId);

  // N + 1 해결을 위한 Query 명시
  @Query("SELECT new org.com.moodbook.bookmark.dto.BookmarkResponseDTO("
      + "b.id, b.title, b.coverImage, b.description, b.categoryName) "
      + "FROM Bookmark bm "
      + "JOIN bm.book b "
      + "WHERE bm.member.id = :memberId")
  List<BookmarkResponseDTO> findAllBookInfoByMemberIdOrderByCreatedAtDesc(Long memberId);

}
