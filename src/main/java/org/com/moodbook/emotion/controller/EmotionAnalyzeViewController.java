package org.com.moodbook.emotion.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.book.dto.BookEmotionAnalyzeResponse;
import org.com.moodbook.book.dto.BookEmotionRecommendAllResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.service.BookService;
import org.com.moodbook.bookchat.dto.ChatRoomResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class EmotionAnalyzeViewController {

  private final BookService bookService;

  @GetMapping("/emotion-analyze")
  public String emotionAnalyzePage(Model model) {
    List<BookEmotionAnalyzeResponse> books = bookService.getAllBooksForEmotionAnalyze();

    model.addAttribute("books", books);
    return "emotion-analyze";
  }

}
