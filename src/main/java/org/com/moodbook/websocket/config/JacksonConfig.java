package org.com.moodbook.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
  public JacksonConfig(ObjectMapper objectMapper) {
    objectMapper.registerModule(new JavaTimeModule());
  }
}