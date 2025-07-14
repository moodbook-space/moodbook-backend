package org.com.moodbook.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FileNameGeneratorImpl implements FileNameGenerator {

  @Override
  public String generateFileName(String originalFilename) {
    String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
    return UUID.randomUUID() + ext;
  }
}