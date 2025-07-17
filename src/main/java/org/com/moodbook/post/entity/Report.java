package org.com.moodbook.post.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.book.entity.Book;


@Entity
@DiscriminatorValue("REPORT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Report extends BasePost {


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = true)
  private Book book;
}

