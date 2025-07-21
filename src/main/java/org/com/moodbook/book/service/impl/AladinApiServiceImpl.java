package org.com.moodbook.book.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.book.dto.AladinBookResponse;
import org.com.moodbook.book.repository.BookRepository;
import org.com.moodbook.book.service.AladinApiService;
import org.com.moodbook.common.config.AladinApiProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AladinApiServiceImpl implements AladinApiService {

  private final AladinApiProperties aladinApiProperties;
  private final BookRepository bookRepository;

  public AladinBookResponse fetchBookByIsbn13(String isbn13) {
    try {
      String apiUrl = aladinApiProperties.getBaseUrl()
          + "?ttbkey=" + aladinApiProperties.getKey()
          + "&itemIdType=ISBN13"
          + "&ItemId=" + isbn13
          + "&SearchTarget=Book"
          + "&output=js"
          + "&Version=20131101";

      HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
      conn.setRequestMethod("GET");

      if (conn.getResponseCode() != 200)
        return null;

      BufferedReader reader = new BufferedReader(
          new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null)
        sb.append(line);
      reader.close();

      String jsonString = sb.toString()
          .replaceFirst("TTB_ItemLookUp\\(", "")
          .replaceFirst("\\);?$", "");

      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
      JsonArray items = jsonObject.getAsJsonArray("item");
      if (items == null || items.isEmpty())
        return null;
      JsonObject obj = items.get(0).getAsJsonObject();

      return AladinBookResponse.builder()
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
