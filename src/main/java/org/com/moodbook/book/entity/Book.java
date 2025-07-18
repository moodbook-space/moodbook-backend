package org.com.moodbook.book.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.model.BaseTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Book extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String isbn13;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(length = 50)
  private String publisher;

  @Column(length = 50)
  private String pubDate;

  @Column(nullable = false, precision = 3, scale = 1)
  private BigDecimal reputation;

  @Column
  private String coverImage;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String categoryName;

  /** 연관관계 매핑 추가 **/
  @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
  private BookCount bookCount;

  public void setBookCount(BookCount bookCount) {
    this.bookCount = bookCount;
    bookCount.setBook(this);
  }
}
