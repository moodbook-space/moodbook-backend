package org.com.moodbook.bookmark.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.common.model.BaseTime;
import org.com.moodbook.member.entity.Member;

@Entity
@Getter
@Table(name = "bookmark", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "book_id"})})
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  // Factory Method. memberId, bookId기반으로 Bookmark Entity를 생성한다.
  public static Bookmark of(Long memberId, Long bookId) {
    return Bookmark.builder().member(Member.builder().id(memberId).build())
        .book(Book.builder().id(bookId).build()).build();
  }

  // 팩토리 메소드 2. member와 book 객체를 받아 Bookmark Entity를 생성한다.
  public static Bookmark of(Member member, Book book) {
    return Bookmark.builder().member(member).book(book).build();
  }

}
