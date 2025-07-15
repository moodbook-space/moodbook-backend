package org.com.moodbook.batch.job;

import static org.com.moodbook.common.constants.CommonConstant.MAX_PAGES_PER_KEYWORD;
import static org.com.moodbook.common.constants.CommonConstant.MAX_RESULTS;
import static org.com.moodbook.common.constants.CommonConstant.TOTAL_COUNT;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.batch.dto.BatchBookResponse;
import org.com.moodbook.common.config.AladinApiProperties;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@StepScope
@Slf4j
public class BookApiReader implements ItemReader<BatchBookResponse> {

    private final AladinApiProperties aladinApiProperties;

    private List<BatchBookResponse> bookList;
    private int index = 0;

    private static final String[] EMOTION_KEYWORDS = {
        "행복", "슬픔", "우울", "분노", "그리움", "외로움", "불안", "공감", "힐링"
    };

    private static final String[] TOPIC_KEYWORDS = {
        "자기계발", "관계", "치유", "심리", "성장", "에세이", "철학", "자존감", "인공지능", "과학"
    };

    public BookApiReader(AladinApiProperties aladinApiProperties) {
        this.aladinApiProperties = aladinApiProperties;
    }

	@Override
    public BatchBookResponse read() {
        if (bookList == null) {
            log.info("알라딘 API에서 데이터 수집 시작");
            bookList = fetchFromAladinApi();    // 이 시점에서 Lazy하게 호출
        }

        if (index < bookList.size()) {
            BatchBookResponse book = bookList.get(index++);
            return book;
        }

        return null;
    }

    private List<BatchBookResponse> fetchFromAladinApi() {
        List<BatchBookResponse> collectedBooks = new ArrayList<>();
        Set<String> seenIsbn13 = new HashSet<>();

        // 키워드 랜덤 셔플
        List<String> keywordList = generateShuffledKeywordList();

        // 페이지 번호 셔플
        List<Integer> pageList = generateShuffledPageList();

        Gson gson = new Gson();

        try {
            for (String keyword : keywordList) {
                Collections.shuffle(pageList);                 // 매 키워드마다 랜덤 페이지 순서

                for (int page : pageList) {
                    if (collectedBooks.size() >= TOTAL_COUNT) break;

                    List<BatchBookResponse> books = fetchBooksByKeywordAndPage(keyword, page, gson, seenIsbn13);

                    for (BatchBookResponse book : books) {
                        if (collectedBooks.size() >= TOTAL_COUNT) break;
                        collectedBooks.add(book);
                    }

                    if (collectedBooks.size() >= TOTAL_COUNT) break;
                }

                if (collectedBooks.size() >= TOTAL_COUNT) break;
            }
        } catch (Exception e) {
            throw new RuntimeException("알라딘 API 수집 중 오류 발생: " + e.getMessage(), e);
        }

        log.info("수집된 책 개수: {}", collectedBooks.size());
        return collectedBooks;
    }

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

    private List<String> generateShuffledKeywordList() {
        List<String> keywordList = new ArrayList<>();
        for (String emotion : EMOTION_KEYWORDS) {
            for (String topic : TOPIC_KEYWORDS) {
                keywordList.add(emotion + " " + topic);
            }
        }
        Collections.shuffle(keywordList);
        return keywordList;
    }

    private List<Integer> generateShuffledPageList() {
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i <= MAX_PAGES_PER_KEYWORD; i++) {
            pages.add(i);
        }
        Collections.shuffle(pages);
        return pages;
    }

    private List<BatchBookResponse> fetchBooksByKeywordAndPage(String keyword, int page,
        Gson gson, Set<String> seenIsbn13) {

        List<BatchBookResponse> result = new ArrayList<>();

        try {
            String apiUrl = buildApiUrl(keyword, page);
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return result;
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            String jsonString = sb.toString()
                .replaceFirst("TTB_ItemSearch\\(", "")
                .replaceFirst("\\);?$", "");

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("item");

            for (JsonElement element : items) {
                JsonObject obj = element.getAsJsonObject();
                String isbn13 = obj.get("isbn13").getAsString();

                if (isbn13 == null || isbn13.isEmpty() || seenIsbn13.contains(isbn13)) continue;

                seenIsbn13.add(isbn13);

                BatchBookResponse book = BatchBookResponse.builder()
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

                result.add(book);
                Thread.sleep(100);
            }

        } catch (Exception e) {
            log.warn("![API 실패] keyword='{}', page={} 에서 오류 발생: {}", keyword, page, e.getMessage());
        }

        return result;
    }

}
