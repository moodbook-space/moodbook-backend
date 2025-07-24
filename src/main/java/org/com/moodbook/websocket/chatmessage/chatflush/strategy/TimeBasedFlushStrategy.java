package org.com.moodbook.websocket.chatmessage.chatflush.strategy;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.service.ChatMessageService;
import org.com.moodbook.websocket.chatmessage.redis.ChatMessageRedisRepository;

@RequiredArgsConstructor
public class TimeBasedFlushStrategy implements ChatFlushStrategy {

  private final ChatMessageRedisRepository redisRepository;
  private final ChatMessageService chatMessageService;
  private final Duration flushInterval;

  @Override
  public void onMessageSaved(Long roomId) {
    // 시간 기반은 메시지 저장시 별도 작업 없음
  }

  @Override
  public void onRoomEmpty(Long roomId) {
    chatMessageService.flushToMongo(roomId);
  }

  @Override
  public void onScheduledFlush() {
    for (Long roomId : redisRepository.getAllActiveRoomIds()) {
      chatMessageService.flushToMongo(roomId);
    }
  }
}