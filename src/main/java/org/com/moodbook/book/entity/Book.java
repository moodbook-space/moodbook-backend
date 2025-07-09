package org.com.moodbook.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Language;
import org.com.moodbook.user.entity.BaseTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(length = 50)
  private String publisher;

  // precision = 총 자리수
  // scale = 소수점 아래 몇 자리까지 표현할지
  @Column(precision = 3, scale = 1)
  private BigDecimal reputation;

  @Column(length = 255)
  private String cover_image;

  // MySQL에서 명시적으로 TEXT형으로 생성
  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String genre;

  @Column(nullable = false)
  private int page_count;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Language language;

  @Column
  private LocalDate published_at;

  @Column(nullable = false, precision = 3, scale = 1)
  private BigDecimal recommendation_tag;

  @Column(nullable = false)
  private Boolean is_cached;

}
