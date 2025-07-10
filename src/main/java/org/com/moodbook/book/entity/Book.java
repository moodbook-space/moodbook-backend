package org.com.moodbook.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.user.entity.BaseTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
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

  // precision = 총 자리수
  // scale = 소수점 아래 몇 자리까지 표현할지
  @Column(nullable = false, precision = 3, scale = 1)
  private BigDecimal reputation;

  @Column(length = 255)
  private String coverImage;

  // MySQL에서 명시적으로 TEXT형으로 생성
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String categoryName;

}
