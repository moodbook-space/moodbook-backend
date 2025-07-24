package org.com.moodbook.post.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

@Configuration
public class ElasticsearchConfig {

  @Value("${spring.elasticsearch.host}")
  private String host;

  @Bean
  public RestClient restClient() {
    return RestClient.builder(
        HttpHost.create(host)
    ).build();
  }

  @Bean
  public ElasticsearchClient elasticsearchClient(RestClient restClient,
      ObjectMapper springMapper) {
    // springMapper 에는 jackson-datatype-jsr310 모듈이 이미 등록돼 있습니다
    JacksonJsonpMapper mapper = new JacksonJsonpMapper(springMapper);
    RestClientTransport transport = new RestClientTransport(restClient, mapper);
    return new ElasticsearchClient(transport);
  }

}
