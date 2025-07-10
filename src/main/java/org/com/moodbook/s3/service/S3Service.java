package org.com.moodbook.s3.service;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.util.FileNameGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

  // Amazon S3 클라이언트 주입
  private final S3Client s3Client;

  // 파일명을 UUID로 변환해주는 유틸 인터페이스
  private final FileNameGenerator fileNameGenerator;

  // S3 버킷 이름 (application.yml에서 주입받음)
  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.region.static}")
  private String region;

  /**
   * S3에 파일 업로드
   * @param file Multipart 형식의 업로드 파일
   * @return 업로드된 파일의 S3 URL
   */
  public String upload(MultipartFile file) {

    // 원본 파일명 추출
    // cute-cat.png
    String originalName = file.getOriginalFilename();

    // UUID 기반 파일명 생성
    // 9bcd1234-5678-90ab-cdef-1234567890ff.png
    String convertedName = fileNameGenerator.generateFileName(originalName);

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(convertedName)
          .build();
      s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    } catch (IOException e) {
      // 업로드 도중 예외 발생 시 런타임 예외로 감싸서 throw
      throw new RuntimeException("S3 업로드 중 오류 발생", e);
    }

    // 업로드된 파일의 URL 반환
    // https://moodbook-bucket.s3.ap-northeast-2.amazonaws.com/9bcd1234-5678-90ab-cdef-1234567890ff.png
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, convertedName);
  }

  /**
   * S3에 해당 파일이 존재하는지 확인
   * @param filename 확인할 파일 이름 (Key)
   * @return 존재 여부
   */
  public boolean doesObjectExist(String filename) {
    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(bucket)
          .key(filename)
          .build();
      s3Client.headObject(headObjectRequest);
      return true;
    } catch (S3Exception e) {
      return false;
    }
  }

  /**
   * S3에서 파일의 public URL 가져오기
   * @param filename 가져올 파일 이름
   * @return S3 URL
   */
  public String getFileUrl(String filename) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, filename);
  }
}