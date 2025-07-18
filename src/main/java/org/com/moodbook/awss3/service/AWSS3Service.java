package org.com.moodbook.awss3.service;

import org.com.moodbook.awss3.dto.AWSS3DTO;
import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

  AWSS3DTO uploadFile(MultipartFile file);

  void deleteFile(AWSS3DTO awss3DTO);

  void doesObjectExist(AWSS3DTO awss3DTO);
}
