package org.com.moodbook.book.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.AIResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.AiSearchService;
import org.com.moodbook.common.config.AladinApiProperties;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.emotion.dto.BookEmotionScoreRequest;
import org.com.moodbook.emotion.service.BookEmotionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSearchServiceImpl implements AiSearchService {

  private final AladinApiProperties aladinApiProperties;
  private final BookRepository bookRepository;
  private final BookEmotionService bookEmotionService;

  @Value("${openai.api-key}")
  private String openaiApiKey;

  @Value("${openai.url}")
  private String openaiUrl;

  @Value("${openai.model}")
  private String model;

  @Override
  public AIResponse askQuestion(String prompt) {

    final ObjectMapper mapper = new ObjectMapper();

    List<Map<String, String>> messages = new ArrayList<>();
    messages.add(Map.of("role", "developer", "content",
        "당신은 감정 기반 독서 추천 도우미입니다. 사용자의 입력을 받으면 다음 두 가지를 개행 문자로 구분하여 생성해주세요.  1. 사용자 감정을 위로하는 자연어 문장  2. ,로 구분된 사용자의 감정에 추천하는 (알라딘 페이지에 있을 만한)대중적인 한글 책 isbn13 3개 "));
    messages.add(Map.of("role", "user", "content", "나 우울해"));
    messages.add(Map.of("role", "assistant", "content",
        "우울한 마음을 따뜻하게 감싸 안아주며 조용히 위로받고 싶은 밤에 어울리는 책을 추천합니다.\n9788990982704,9788990982315,9791170612759"));
    messages.add(Map.of("role", "user", "content", prompt));



    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", model);
    requestBody.put("input", messages);

    String contentMessage = null; // 위로 문장
    List<String> isbnList = new ArrayList<>();
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(openaiUrl))
          .header("Authorization", "Bearer " + openaiApiKey)
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
// OpenAI 응답 파싱
      String fullResponse = response.body(); // OpenAI 응답 전체를 JSON 문자열로 받음

// 1. 응답 JSON 구조에서 'choices[].message.content'만 추출
      JsonNode root = mapper.readTree(fullResponse);
      String gptContent = root
          .path("output")
          .get(0)
          .path("content")
          .get(0)
          .path("text")
          .asText();  // 위로 문장과 isbn13 문자열이 개행으로 구분되어 있음
// 2. 개행 기준으로 분리
      String[] parts = gptContent.split("\n");
      contentMessage = parts.length > 0 ? parts[0].trim() : "";
      if (parts.length > 1) {
        isbnList = Arrays.stream(parts[1].split(","))
            .map(String::trim)
            .toList();
      }

    } catch (Exception e) {

      throw new RuntimeException("openAi search 오류 발생");
    }

    //보여줄 책List
    List<BookResponse> bookList = new ArrayList<>();

    int targetSize = 3;
    int retryCount = 0;
    int maxRetries = 5;

    Queue<String> isbnQueue = new LinkedList<>(isbnList);

    while (bookList.size() < targetSize && (!isbnQueue.isEmpty() || retryCount < maxRetries)) {
      String isbn13 = isbnQueue.isEmpty() ? fetchReplacementIsbn(prompt) : isbnQueue.poll();
      if (isbn13 == null || isbn13.isBlank()) {
        retryCount++;
        continue;
      }

      Book book = bookRepository.findByIsbn13(isbn13);
      BookResponse bookForInput;

      if (book != null) {
        bookForInput = BookResponse.builder()
            .bookId(book.getId())
            .isbn13(book.getIsbn13())
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .pubDate(book.getPubDate())
            .reputation(book.getReputation())
            .coverImage(book.getCoverImage())
            .description(book.getDescription())
            .categoryName(book.getCategoryName())
            .createdAt(book.getCreatedAt())
            .build();
      } else {
        bookForInput = fetchBookByIsbn13(isbn13);
        if (bookForInput == null) {
          log.info("ISBN {} 알라딘 API에서 조회 실패 → 대체 요청 예정", isbn13);
          retryCount++;
          continue;
        }

        bookRepository.save(Book.builder()
            .isbn13(bookForInput.getIsbn13())
            .title(bookForInput.getTitle())
            .author(bookForInput.getAuthor())
            .publisher(bookForInput.getPublisher())
            .pubDate(bookForInput.getPubDate())
            .reputation(bookForInput.getReputation())
            .coverImage(bookForInput.getCoverImage())
            .description(bookForInput.getDescription())
            .categoryName(bookForInput.getCategoryName())
            .build());
      }

      bookList.add(bookForInput);

      try {
        bookEmotionService.saveEmotionScore(BookEmotionScoreRequest.builder()
            .bookId(bookForInput.getBookId())
            .isbn13(bookForInput.getIsbn13())
            .bookTitle(bookForInput.getTitle())
            .description(bookForInput.getDescription())
            .build());
      } catch (Exception e) {
        log.warn("감정 점수 저장 실패: {}", e.getMessage());
      }
    }




    return new AIResponse(contentMessage, bookList);
  }

  public BookResponse fetchBookByIsbn13(String isbn13) {
    try {
      String apiUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx"
          + "?ttbkey=" + aladinApiProperties.getKey()
          + "&itemIdType=ISBN13"
          + "&ItemId=" + isbn13
          + "&SearchTarget=Book"
          + "&output=js"
          + "&Version=20131101";

      HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
      conn.setRequestMethod("GET");

      if (conn.getResponseCode() != 200) {
        return null;
      }

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      reader.close();

      String jsonString = sb.toString()
          .replaceFirst("TTB_ItemLookUp\\(", "")
          .replaceFirst("\\);?$", "");

      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
      JsonArray items = jsonObject.getAsJsonArray("item");
      if (items == null || items.isEmpty()) {
        return null;
      }
      JsonObject obj = items.get(0).getAsJsonObject();

      return BookResponse.builder()
          .title(obj.get("title").getAsString())
          .isbn13(obj.get("isbn13").getAsString())
          .author(obj.get("author").getAsString())
          .publisher(obj.get("publisher").getAsString())
          .pubDate(obj.get("pubDate").getAsString())
          .reputation(BigDecimal.valueOf(obj.get("customerReviewRank").getAsInt()))
          .coverImage(obj.get("cover").getAsString())
          .description(obj.get("description").getAsString())
          .categoryName(obj.get("categoryName").getAsString())
          .build();

    } catch (Exception e) {
      log.warn("알라딘 ISBN13 단일조회 실패: {}", e.getMessage());
      return null;
    }
  }
  private String fetchReplacementIsbn(String prompt) {
    try {
      List<Map<String, String>> messages = new ArrayList<>();
      messages.add(Map.of("role", "user", "content", prompt));
      messages.add(Map.of("role", "system", "content",
          "입력한 감정에 맞는 한국 도서 ISBN13 한 개만 반환해주세요. 숫자만, 쉼표 없이."));

      Map<String, Object> body = new HashMap<>();
      body.put("model", model);
      body.put("input", messages);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(openaiUrl))
          .header("Authorization", "Bearer " + openaiApiKey)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(body)))
          .build();

      HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      JsonNode root = new ObjectMapper().readTree(response.body());

      return root.path("output").get(0).path("content").get(0).path("text").asText().trim();

    } catch (Exception e) {
      log.warn("대체 ISBN 요청 실패: {}", e.getMessage());
      return null;
    }
  }



}
