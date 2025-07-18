package org.com.moodbook.awss3.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.awss3.dto.AWSS3DTO;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.util.AWSS3Uploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

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
  public AWSS3DTO uploadFile(MultipartFile file) {
    return AWSS3DTO.of(awsS3Uploader.upload(file));
  }

  /** S3에서 파일을 삭제한다. */
  @Override
  public void deleteFile(AWSS3DTO awss3DTO) {

    // 일단 파일이 있는지 확인한다.
    doesObjectExist(awss3DTO);

    // 그리고 삭제를 수행
    awsS3Uploader.delete(awss3DTO.getUrl());
  }

  @Override
  // 파일이 존재하는지 확인한다. 파일이 없으면 알아서 에러 던지게 됨
  public void doesObjectExist(AWSS3DTO awss3DTO) {
    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket("${aws.s3.bucketName}")
          .key(awss3DTO.getUrl())
          .build();
      s3Client.headObject(headObjectRequest);

    } catch (S3Exception e) {
       // 404에러는 파일이 없을 경우를 나타냄
       if (e.statusCode() == 404)
         throw BaseException.AWSS3_NO_FILE;
    } catch (Exception e) { // 이외에는 그냥 에러
      throw BaseException.INTERNAL_SERVER_ERROR;
    }
  }
}
