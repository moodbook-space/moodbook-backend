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
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.AIResponse;
import org.com.moodbook.book.dto.BookResponse;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.AiSearchService;
import org.com.moodbook.common.config.AladinApiProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiSearchServiceImpl implements AiSearchService {

  private final AladinApiProperties aladinApiProperties;
  private final BookRepository bookRepository;

  @Value("${openai.api-key}")
  private String openaiApiKey;

  @Value("${openai.url}")
  private String openaiUrl;

  @Value("${openai.model}")
  private String model;

  @Override
  public AIResponse askQuestion(String prompt) {
    final ObjectMapper mapper = new ObjectMapper();

    // 1. GPT에게 ISBN13 3개 추천받기
    List<String> isbnList = new ArrayList<>();
    String gptRaw = "";
    try {
      List<Object> messages = List.of(
          java.util.Map.of("role", "system", "content", "감정이나 상황에 맞는 한국 도서 ISBN13 3개만 ,로 구분해서 반환. 설명문구 없이 숫자만 보내!"),
          java.util.Map.of("role", "user", "content", prompt)
      );
      var requestBody = java.util.Map.of("model", model, "messages", messages);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(openaiUrl))
          .header("Authorization", "Bearer " + openaiApiKey)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
          .build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      JsonNode root = mapper.readTree(response.body());
      gptRaw = root.path("choices").get(0).path("message").path("content").asText();
      isbnList = Arrays.stream(gptRaw.split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .toList();

      // 로그: GPT 원본 답변, 추출된 isbn 리스트
      log.info("[GPT 원본 답변] {}", gptRaw);
      log.info("[GPT 추출 ISBN 리스트] {}", isbnList);

    } catch (Exception e) {
      throw new RuntimeException("openAi search 오류 발생", e);
    }

    // 2. 알라딘에서 각 ISBN13로 책 정보 조회
    List<BookResponse> foundBooks = new ArrayList<>();
    List<String> notFoundIsbnList = new ArrayList<>();
    for (String isbn13 : isbnList) {
      BookResponse book = fetchBookByIsbn13(isbn13);
      if (book != null) {
        foundBooks.add(book);
      } else {
        notFoundIsbnList.add(isbn13);
      }
    }
    // 2-1 로그: 알라딘에서 존재/미존재 책
    log.info("[알라딘 존재 책 ISBN] {}", foundBooks.stream().map(BookResponse::getIsbn13).toList());
    log.info("[알라딘 미존재 ISBN] {}", notFoundIsbnList);

    // 3. 존재하는 책들 RDB에 저장(이미 존재하면 패스)
    List<BookResponse> savedBooks = new ArrayList<>();
    for (BookResponse book : foundBooks) {
      if (!bookRepository.existsByIsbn13(book.getIsbn13())) {
        // entity 변환, 저장
        Book saved = bookRepository.save(Book.builder()
            .isbn13(book.getIsbn13())
            .title(book.getTitle())
            .author(book.getAuthor())
            .publisher(book.getPublisher())
            .pubDate(book.getPubDate())
            .reputation(book.getReputation())
            .coverImage(book.getCoverImage())
            .description(book.getDescription())
            .categoryName(book.getCategoryName())
            .build());
        savedBooks.add(BookResponse.from(saved));
      } else {
        // 이미 DB에 있으면, 그냥 BookResponse 변환
        Book exist = bookRepository.findByIsbn13(book.getIsbn13());
        savedBooks.add(BookResponse.from(exist));
      }
    }
    // 3-1 로그: RDB에 저장된 책(페이지에 노출될 책)
    log.info("[RDB 저장 및 반환 책 리스트] {}", savedBooks.stream().map(BookResponse::getIsbn13).toList());

    // 4. 저장된 책 리스트를 페이지(응답)에 노출
    String message = savedBooks.isEmpty()
        ? "추천 도서를 찾지 못했습니다."
        : "추천 도서입니다!";

    return new AIResponse(message, savedBooks);
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

}
