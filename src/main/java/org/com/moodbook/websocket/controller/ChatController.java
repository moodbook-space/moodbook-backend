package org.com.moodbook.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.websocket.dto.ChatMessage;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat.sendMessage")
  public void sendMessage(ChatMessage message) {
    // 여기서 roomId를 이용해 동적으로 destination 지정!
    String destination = "/topic/room." + message.getRoomId();
    messagingTemplate.convertAndSend(destination, message);
  }
}
