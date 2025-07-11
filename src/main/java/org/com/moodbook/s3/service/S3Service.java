package org.com.moodbook.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
  boolean doesObjectExist(String filename);
  String getFileUrl(String filename);
  String upload(MultipartFile file);

}
