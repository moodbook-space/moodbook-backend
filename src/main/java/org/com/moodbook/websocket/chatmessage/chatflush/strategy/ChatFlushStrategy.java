package org.com.moodbook.websocket.chatmessage.chatflush.strategy;

public interface ChatFlushStrategy {
  void onMessageSaved(Long roomId);
  void onRoomEmpty(Long roomId);
  void onScheduledFlush();
}
