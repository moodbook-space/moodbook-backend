package org.com.moodbook.common.util;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AWSS3UploaderImpl implements AWSS3Uploader {

  // Amazon S3 클라이언트
  private final S3Client s3Client;

  // 파일명을 UUID로 변환해주는 유틸 인터페이스
  private final FileNameGenerator fileNameGenerator;

  // S3 버킷 이름과 지역 (application.yml에 설정됨)
  @Value("${cloud.aws.s3.bucketName}")
  private String bucket;

  @Value("${cloud.aws.region.static}")
  private String region;

  /**
   * S3에 파일 업로드
   *
   * @param file Multipart 형식의 업로드 파일
   * @return 업로드된 파일의 S3 URL
   */
  @Override
  public String upload(MultipartFile file) {
    // 원본 파일명 추출
    // cute-cat.png
    String originalName = file.getOriginalFilename();

    // UUID 기반 파일명으로 변경
    // 9bcd1234-5678-90ab-cdef-1234567890ff.png
    String convertedName = fileNameGenerator.generateFileName(originalName);

    try {
      // Object를 넣는 요청을 보낸다. 버킷명은 위에서 지정했고, 키는 UUID기반 파일명으로
      PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket)
          .key(convertedName).build();

      // S3Client를 통해 위에서 build한 Request를 보낸다.(저장 수행됨)
      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      log.info("업로드 완료");
    } catch (IOException e) {
      // 업로드 중 오류 발생 시 에러 던지기
      throw BaseException.AWSS3_UPLOAD_ERROR;
    }

    // 업로드된 파일의 URL 반환
    // https://moodbook-bucket.s3.ap-northeast-2.amazonaws.com/9bcd1234-5678-90ab-cdef-1234567890ff.png
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, convertedName);
  }
}
