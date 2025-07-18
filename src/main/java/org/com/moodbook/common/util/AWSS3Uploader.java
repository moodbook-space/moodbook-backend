package org.com.moodbook.common.util;

import org.com.moodbook.awss3.dto.AWSS3DTO;
import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Uploader {

  String upload(MultipartFile file);

  void delete(String url);
}
