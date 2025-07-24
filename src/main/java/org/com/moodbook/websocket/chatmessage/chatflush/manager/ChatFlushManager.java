package org.com.moodbook.websocket.chatmessage.chatflush.manager;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.chatmessage.chatflush.strategy.ChatFlushStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatFlushManager {

  private final ChatFlushStrategy strategy;

  // 이벤트 발생시 호출
  public void onMessageSaved(Long roomId) {
    strategy.onMessageSaved(roomId);
  }

  public void onRoomEmpty(Long roomId) {
    strategy.onRoomEmpty(roomId);
  }

  public void onScheduledFlush() {
    strategy.onScheduledFlush();
  }
}