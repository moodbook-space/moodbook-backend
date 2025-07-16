package org.com.moodbook.websocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.websocket.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatMessageController {

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/room.{roomId}")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    // 저장 로직 등 필요시 추가
    return chatMessage; // 그대로 전송
  }

}
