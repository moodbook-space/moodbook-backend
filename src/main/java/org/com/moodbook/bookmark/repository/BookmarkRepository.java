package org.com.moodbook.bookmark.repository;

import java.util.List;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

  // 유저번호를 받아 북마크에 등록된 모든 책 가져오기
  @Query("SELECT b "
      + "FROM Bookmark bm "
      + "JOIN bm.book b "
      + "WHERE bm.member.id = :memberId")
  List<Book> findBooksByMemberId(@Param("memberId") Long memberId);
}
