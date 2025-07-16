package org.com.moodbook.bookmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.member.entity.Member;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private boolean isDeleated = false;

  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  // Factory Method. memberId, bookId기반으로 Entity를 생성하고 Bookmark에 넣는다.
  public static Bookmark of(Long memberId, Long bookId) {
    return Bookmark.builder()
        .member(Member.builder()
            .id(memberId).build())
        .book(Book.builder()
            .id(bookId).build())
        .build();
  }

}
