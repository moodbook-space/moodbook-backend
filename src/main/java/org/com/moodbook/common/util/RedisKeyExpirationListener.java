package org.com.moodbook.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.security.authentication.service.EmailAuthenticationService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyExpirationListener implements MessageListener {

  private final EmailAuthenticationService emailAuthenticationService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String expiredKey = message.toString();
    log.info("[Redis 만료 이벤트 감지] expired key: {}", expiredKey);

    if (expiredKey.startsWith("email-verification:")) {
      emailAuthenticationService.cleanupByExpiredToken(expiredKey);
    }

  }

  /**
   * Redis에서 TTL이 만료된 키가 발생했을 때 자동 호출되는 메서드
   */

}
