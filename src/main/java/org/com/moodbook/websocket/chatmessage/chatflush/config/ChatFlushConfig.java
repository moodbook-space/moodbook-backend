package org.com.moodbook.websocket.chatmessage.chatflush.config;

import java.time.Duration;
import org.com.moodbook.websocket.chatmessage.chatflush.strategy.ChatFlushStrategy;
import org.com.moodbook.websocket.chatmessage.chatflush.strategy.TimeBasedFlushStrategy;
import org.com.moodbook.websocket.chatmessage.service.ChatMessageService;
import org.com.moodbook.websocket.chatmessage.redis.ChatMessageRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatFlushConfig {
  @Bean
  public ChatFlushStrategy chatFlushStrategy(ChatMessageRedisRepository redisRepository, ChatMessageService chatMessageService) {
    // 메시지 100개 쌓이면 flush
    // return new CountBasedFlushStrategy(redisRepository, chatMessageService, 100);

    // 10분마다 flush하는 전략으로 교체
    return new TimeBasedFlushStrategy(redisRepository, chatMessageService, Duration.ofMinutes(10));
  }
}