package org.com.moodbook.awss3.service;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

  boolean doesObjectExist(String filename);

  String getFileUrl(String filename);

  String uploadFile(MultipartFile file);
}
