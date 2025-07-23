package org.com.moodbook.book.eventlistener.event;

import java.util.List;
import lombok.Getter;
import org.com.moodbook.book.entity.Book;


@Getter
public class BookCreatedEvent {

  private final List<Book> books;

  public BookCreatedEvent(List<Book> books) { this.books = books; }

}
