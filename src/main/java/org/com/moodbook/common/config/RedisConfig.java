package org.com.moodbook.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  @Value("${spring.profiles.active:local}")
  private String activeProfile;

  //redis 연결 팩토리 설정
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);

    LettuceClientConfiguration clientConfig;

    if ("prod".equals(activeProfile)) {
      clientConfig = LettuceClientConfiguration.builder()
          .useSsl()
          .disablePeerVerification() // 운영 인증서 신뢰 설정은 실환경에 맞게 구성
          .build();
    } else {
      clientConfig = LettuceClientConfiguration.builder().build();
    }

    return new LettuceConnectionFactory(redisConfig, clientConfig); // application.yml의 host/port를 따름
  }

  //RedisTemplate 설정(Key,Value모두 String 기반)
  @Bean
  public RedisTemplate<String, String > redisTemplate() {
    RedisTemplate<String, String > redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());

    //문자열 기반 직렬화 설정(key,Value 동일하게)
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());

    return redisTemplate;
  }
  //
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
    // Redis의 pub/sub 메시지를 수신하고 처리하는 리스너 컨테이너 객체를 생성
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    // Redis와 연결할 . 있도록 redisConnectionFactory를 생성
    // 이 factory는 Redis 서버에 연결하는 역할
    container.setConnectionFactory(redisConnectionFactory);
    // 구성된 컨테이너를 반환해서 Spring이 관리하도하도록 하는 역활
    // 이 컨테이너는 TTL 만료 등 Redis에서 발생한 이벤트를 수신
    return container;
  }


}
