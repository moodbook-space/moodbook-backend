package org.com.moodbook.awss3.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.util.AWSS3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class AWSS3ServiceImpl implements AWSS3Service {

  // AWS 파일 업로다
  private final AWSS3Uploader awsS3Uploader;
  private final S3Client s3Client;

  /**
   * S3에 파일을 업로드한다. 동작은 awsS3Uploader가 수행함.
   * @param file Multipart 형식의 업로드 파일
   * @return 업로드된 파일의 S3 URL
   */
  @Override
  public String uploadFile(MultipartFile file) {
    return awsS3Uploader.upload(file);
  }

  /**
   * S3에 해당 파일이 존재하는지 확인
   * @param filename 확인할 파일 이름 (Key)
   * @return 존재 여부(bool)
   */
  @Override
  public boolean doesObjectExist(String filename) {
    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket("${aws.s3.bucketName}")
          .key(filename)
          .build();
      s3Client.headObject(headObjectRequest);
      return true;

    } catch (Exception e) {
      throw BaseException.AWSS3_GET_ERROR;
    }
  }

  /**
   * S3에서 파일명 기반으로 public URL 가져오기
   * @param filename 가져올 파일 이름
   * @return S3 URL
   */
  @Override
  public String getFileUrl(String filename) {
    return String.format("https://%s.s3.%s.amazonaws.com/%s", "${cloud.aws.s3.bucketName}", "${cloud.aws.region.static}", filename);
  }
}
