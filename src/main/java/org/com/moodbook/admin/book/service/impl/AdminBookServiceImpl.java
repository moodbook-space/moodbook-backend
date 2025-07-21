package org.com.moodbook.admin.book.service.impl;

import static org.com.moodbook.common.constants.CommonConstant.MAX_RESULTS;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.admin.book.dto.AdminBookDTO;
import org.com.moodbook.admin.book.repository.AdminBookRepository;
import org.com.moodbook.admin.book.service.AdminBookService;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.com.moodbook.batch.job.BookApiReader;
import org.com.moodbook.book.entity.Book;
import org.com.moodbook.book.entity.BookCount;
import org.com.moodbook.common.config.AladinApiProperties;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminBookServiceImpl implements AdminBookService {

  private final AladinApiProperties aladinApiProperties;
  private final AdminBookRepository adminBookRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<AdminBookDTO> getBookList(Pageable pageable) {
    return adminBookRepository.findAllPaging(pageable);
  }

  @Override
  public Page<AdminBookDTO> searchBooks(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      return adminBookRepository.findAllPaging(pageable); // 기존 전체 목록
    }
    return adminBookRepository.find(query, pageable);
  }

  @Override
  public void deleteById(Long bookId) {
    if (!adminBookRepository.existsById(bookId)) {
      throw new BaseException(ErrorCode.CHATROOM_NOT_FOUND);
    }
    adminBookRepository.deleteById(bookId);
  }

  @Override
  public List<BatchBookResponse> aladinBookSearch(String keyword) {
    Gson gson = new Gson();
    Set<String> seenIsbn13 = new HashSet<>();
    List<BatchBookResponse> result = new ArrayList<>();

    try {
      String apiUrl = buildApiUrl(keyword, 1);
      HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
      conn.setRequestMethod("GET");

      if (conn.getResponseCode() != 200) {
        return result;
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
          .replaceFirst("TTB_ItemSearch\\(", "")
          .replaceFirst("\\);?$", "");

      JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
      JsonArray items = jsonObject.getAsJsonArray("item");

      for (JsonElement element : items) {
        JsonObject obj = element.getAsJsonObject();
        String isbn13 = obj.get("isbn13").getAsString();

        if (isbn13 == null || isbn13.isEmpty() || seenIsbn13.contains(isbn13)) {
          continue;
        }

        seenIsbn13.add(isbn13);

        BatchBookResponse book = BatchBookResponse.builder()
            .title(obj.get("isbn13").getAsString())
            .isbn13(obj.get("title").getAsString())
            .author(obj.get("author").getAsString())
            .publisher(obj.get("publisher").getAsString())
            .pubDate(obj.get("pubDate").getAsString())
            .reputation(BigDecimal.valueOf(obj.get("customerReviewRank").getAsInt()))
            .coverImage(obj.get("cover").getAsString())
            .description(obj.get("description").getAsString())
            .categoryName(obj.get("categoryName").getAsString())
            .build();

        result.add(book);
        Thread.sleep(100);
      }

    } catch (Exception e) {
      log.warn("![API 실패] keyword='{}', page={} 에서 오류 발생: {}", keyword, 1, e.getMessage());
    }

    return result;
  }

  @Override
  public void addBook(BatchBookResponse batchBookResponse) {
    Book book = Book.builder()
        .isbn13(batchBookResponse.getIsbn13())
        .title(batchBookResponse.getTitle())
        .author(batchBookResponse.getAuthor())
        .publisher(batchBookResponse.getPublisher())
        .pubDate(batchBookResponse.getPubDate())
        .reputation(batchBookResponse.getReputation())
        .coverImage(batchBookResponse.getCoverImage())
        .description(batchBookResponse.getDescription())
        .categoryName(batchBookResponse.getCategoryName())
        .build();

    BookCount bookCount = BookCount.builder()
        .viewCount(0L)
        .build();

    book.setBookCount(bookCount);

    adminBookRepository.save(book);
  }

  ;

  private String buildApiUrl(String keyword, int page) throws UnsupportedEncodingException {
    return aladinApiProperties.getBaseUrl()
        + "?ttbkey=" + aladinApiProperties.getKey()
        + "&Query=" + URLEncoder.encode(keyword, "UTF-8")
        + "&QueryType=Keyword"
        + "&MaxResults=" + MAX_RESULTS
        + "&start=" + ((page - 1) * MAX_RESULTS + 1)
        + "&SearchTarget=Book"
        + "&Sort=PublishTime"       // 최신순 정렬 추가
        + "&output=js"
        + "&Version=20131101";
  }


}

