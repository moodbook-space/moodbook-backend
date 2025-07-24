package org.com.moodbook.websocket.chatmessage.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.entity.ChatMessageDocument;
import org.com.moodbook.websocket.chatmessage.repository.ChatMessageMongoRepository;
import org.com.moodbook.websocket.dto.ChatMessage;
import org.com.moodbook.websocket.chatmessage.redis.ChatMessageRedisRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageMongoRepository mongoRepository;
  private final ChatMessageRedisRepository redisRepository;


  // 메시지 저장: Redis 우선 저장 (실시간), 플러시로 MongoDB 적재
  public void saveMessage(Long roomId, ChatMessage message) {
    redisRepository.saveMessage(roomId, message);
  }

  public void flushToMongo(Long roomId) {
    List<ChatMessage> messages = redisRepository.getMessagesInRoom(roomId, 0L, (long) -1);
    List<ChatMessageDocument> docs = messages.stream()
        .map(msg -> ChatMessageDocument.builder()
            .roomId(msg.getRoomId())
            .senderId(msg.getSenderId())
            .senderName(msg.getSenderName())
            .message(msg.getMessage())
            .time(msg.getTime())
            .build())
        .collect(Collectors.toList());
    mongoRepository.saveAll(docs);

    redisRepository.deleteAll(roomId); // 플러시 후 캐시 비움
  }

  // MongoDB에서 메시지 조회(페이지네이션)
  public List<ChatMessageDocument> getMessagesFromMongo(Long roomId, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by("time").descending());
    // 내림차순으로 최신이 먼저 오도록 (프론트에서 reverse해서 보여주기!)
    return mongoRepository.findByRoomIdOrderByTimeAsc(roomId, pageRequest);
  }

  // Redis에서 최근 메시지 조회 (실시간 최신 메시지만, 페이지네이션 X)
  public List<ChatMessage> getRecentMessagesFromRedis(Long roomId, Long count) {
    // Redis는 마지막 count개만(최신순, right 기준)
    return redisRepository.getMessagesInRoom(roomId, -count, (long) -1);
  }

}
