package org.com.moodbook.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean
  public S3Client s3Client() {
    // application.yml 또는 properties에서 주입받은 accessKey, secretKey로 AWS 자격 증명 객체 생성
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    return S3Client.builder()
        // AWS 리전 설정 (예: ap-northeast-2)
        .region(Region.of(region))
        // 정적 자격 증명 제공자에 위에서 만든 credentials를 설정
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        // 최종적으로 S3Client 객체 생성
        .build();
  }
}
