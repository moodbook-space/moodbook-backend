package org.com.moodbook.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidFileNameGenerator implements FileNameGenerator {

  @Override
  public String generateFileName(String originalFilename) {
    String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
    return UUID.randomUUID().toString() + ext;
  }
}
