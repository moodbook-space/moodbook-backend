package org.com.moodbook.websocket.chatmessage.redis;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.service.ChatMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageCache {

  private final ChatMessageService chatMessageService;
  private final ChatMessageRedisRepository redisRepository;


  // 10분마다 모든 방 flush
  @Scheduled(fixedDelay = 10 * 60 * 1000)
  public void scheduledFlushAllRooms() {
    for (Long roomId : redisRepository.getAllActiveRoomIds()) {
      chatMessageService.flushToMongo(roomId);
    }
  }

  // 마지막 사용자가 퇴장할 때 flush
  public void onRoomEmpty(Long roomId) {
    chatMessageService.flushToMongo(roomId);
  }
}
