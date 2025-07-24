package org.com.moodbook.websocket.chatmessage.chatflush.strategy;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.service.ChatMessageService;
import org.com.moodbook.websocket.chatmessage.redis.ChatMessageRedisRepository;

@RequiredArgsConstructor
public class CountBasedFlushStrategy implements ChatFlushStrategy {

  private final ChatMessageRedisRepository redisRepository;
  private final ChatMessageService chatMessageService;
  private final int threshold; // e.g. 100

  @Override
  public void onMessageSaved(Long roomId) {
    if (redisRepository.countMessagesInRoom(roomId) >= threshold) {
      chatMessageService.flushToMongo(roomId);
    }
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