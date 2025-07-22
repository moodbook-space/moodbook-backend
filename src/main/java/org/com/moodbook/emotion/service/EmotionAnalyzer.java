package org.com.moodbook.emotion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmotionAnalyzer {

  @Value("${openai.api-key}")
  private String openaiApiKey;

  @Value("${openai.url}")
  private String openaiUrl;

  @Value("${openai.model}")
  private String model;

  private static final List<String> EMOTION_TAGS = Arrays.asList(
      "기쁨", "슬픔", "분노", "불안", "설렘", "위로", "외로움", "감동", "행복"
  );

  @Transactional
  public Map<String, Integer> analyzeEmotion(String description) throws Exception {
    String prompt = String.format(
        "아래 문장에 대해 9가지 감정 태그(기쁨, 슬픔, 분노, 불안, 설렘, 위로, 외로움, 감동, 행복) 각각에 대해 0~5점으로 점수를 부여해서 JSON 형식으로 답해줘. 문장: '%s', 예시: {\"기쁨\":0, \"슬픔\":5, \"분노\":1, \"불안\":2, \"설렘\":0, \"위로\":1, \"외로움\":4, \"감동\":0, \"행복\":0}", description);

    Map<String, Object> systemMsg = Map.of("role", "system", "content", "너는 감정 분석 전문가야. 반드시 JSON 형식만으로 결과를 반환해야 해.");
    Map<String, Object> userMsg = Map.of("role", "user", "content", prompt);
    Map<String, Object> bodyMap = new HashMap<>();
    bodyMap.put("model", model);
    bodyMap.put("input", List.of(systemMsg, userMsg));

    ObjectMapper mapper = new ObjectMapper();
    String requestBody = mapper.writeValueAsString(bodyMap);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(openaiUrl))
        .header("Authorization", "Bearer " + openaiApiKey)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("gpt 응답: " + response.body());

    JsonNode root = mapper.readTree(response.body());
    JsonNode output = root.path("output");
    if (!output.isArray() || output.size() == 0) {
      throw new IllegalStateException("GPT 응답에 choices가 없음");
    }

    String content = output.get(0).path("content").get(0).path("text").asText();
    content = content.replaceAll("(?i)```json", "").replaceAll("(?i)```", "").trim();

    Pattern jsonPattern = Pattern.compile("\\{[\\s\\S]*?\\}");
    Matcher matcher = jsonPattern.matcher(content);
    String jsonString = null;
    if (matcher.find()) {
      jsonString = matcher.group();
    } else {
      throw new RuntimeException("GPT 응답에서 JSON을 추출하지 못함: " + content);
    }

    Map<String, Integer> result = new HashMap<>();
    try {
      JsonNode scoresNode = mapper.readTree(jsonString);
      for (String emotion : EMOTION_TAGS) {
        result.put(emotion, scoresNode.path(emotion).asInt());
      }
    } catch (Exception e) {
      System.err.println("응답에서 JSON 파싱 실패: " + jsonString);
      throw new RuntimeException("감정 분석 응답 파싱 실패: " + jsonString, e);
    }
    return result;
  }
}
