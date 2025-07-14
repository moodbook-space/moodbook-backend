package org.com.moodbook.common.util;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Uploader {

  String upload(MultipartFile file);
}
