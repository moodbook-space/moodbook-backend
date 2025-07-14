package org.com.moodbook.awss3.controller;

import org.com.moodbook.awss3.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class AWSS3Controller {

  private final AWSS3Service awss3Service;

  /// /api/s3/upload 파일을 업로드하고, 업로드 결과로 그 URL을 반환한다.
  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    String url = awss3Service.uploadFile(file);
    return ResponseEntity.ok(url);
  }

  /// 파일명 기반으로 URL을 받아오는 함수 그런데, upload결과로 URL을 반환할거고, 이를 DB에 저장해 쓸 것이기 때문에 사실상 사용할 일은 없음
  public ResponseEntity<?> getFile(@RequestParam String filename) {
    if (!awss3Service.doesObjectExist(filename)) {
      return ResponseEntity.notFound().build();
    }

    String url = awss3Service.getFileUrl(filename);
    return ResponseEntity.ok(url);
  }
}
