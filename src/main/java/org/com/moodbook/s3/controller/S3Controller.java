package org.com.moodbook.s3.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.s3.service.S3ServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

  private final S3ServiceImpl s3ServiceImpl;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    String url = s3ServiceImpl.upload(file);
    return ResponseEntity.ok(url);
  }

  @GetMapping("/getUrl")
  public ResponseEntity<?> getFile(@RequestParam String filename) {
    if (!s3ServiceImpl.doesObjectExist(filename)) {
      return ResponseEntity.notFound().build();
    }

    String url = s3ServiceImpl.getFileUrl(filename);
    return ResponseEntity.ok(url);
  }
}
