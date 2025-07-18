package org.com.moodbook.security.authentication.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisTestController {
  private final RedisTemplate<String, String> redisTemplate;

  @GetMapping("/redis/test")
  public String test() {
    redisTemplate.opsForValue().set("key", "Hello Redis!");
    return redisTemplate.opsForValue().get("key");
  }

}
