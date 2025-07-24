package org.com.moodbook.websocket.chatmessage.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.websocket.dto.ChatMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageRedisRepository {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  private static String getKey(Long roomId) {
    return "chat:room:" + roomId;
  }

  public void saveMessage(Long roomId, ChatMessage message) {
    try {
      String msgJson = objectMapper.writeValueAsString(message);
      redisTemplate.opsForList().rightPush(getKey(roomId), msgJson);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<ChatMessage> getMessagesInRoom(Long roomId, Long start, Long end) {
    List<String> msgJsonList = redisTemplate.opsForList().range(getKey(roomId), start, end);
    if (msgJsonList == null) return List.of();
    return msgJsonList.stream().map(json -> {
      try {
        return objectMapper.readValue(json, ChatMessage.class);
      } catch (Exception e) {
        return null;
      }
    }).collect(Collectors.toList());
  }

  public void deleteAll(Long roomId) {
    redisTemplate.delete(getKey(roomId));
  }

  public Long countMessagesInRoom(Long roomId) {
    String key = "chat:room:" + roomId;
    return redisTemplate.opsForList().size(key);
  }


  // 현재 메시지가 남아있는 모든 방의 roomId 리스트 반환
  public Set<Long> getAllActiveRoomIds() {
    Set<String> keys = redisTemplate.keys("chat:room:*");

    // "chat:room:{roomId}" → roomId만 추출
    return keys.stream()
        .map(k -> k.replace("chat:room:", ""))
        .map(Long::valueOf)
        .collect(Collectors.toSet());
  }

}