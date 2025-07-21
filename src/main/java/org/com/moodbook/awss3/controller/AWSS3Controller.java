package org.com.moodbook.awss3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.awss3.dto.AWSS3DTO;
import org.com.moodbook.awss3.service.AWSS3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "AWS Controller", description = "AWS S3에 파일을 업로드하기 위한 API")
public class AWSS3Controller {

  private final AWSS3Service awss3Service;

  /// /api/s3/upload 파일을 업로드하고, 업로드 결과로 그 URL을 반환한다.
  @PostMapping("/s3/upload")
  @Operation(summary = "S3에 파일 업로드하는 컨트롤러")
  @ApiResponse(responseCode = "200", description = "정상적으로 파일이 업로드 되었습니다.")
  public ResponseEntity<AWSS3DTO> uploadFile(@RequestParam("file") MultipartFile file) {
    AWSS3DTO awss3DTO = awss3Service.uploadFile(file);
    return ResponseEntity.ok(awss3DTO);
  }

  /**
   파일명 기반으로 URL을 받아오는 함수
   그러나, upload결과로 URL이 반환되며, URL 자체를 DB에 저장해 쓸 것이기 때문에 사실상 사용할 일은 없음
   추후 관련 코드가 필요할 일이 생길 경우를 대비해 주석처리함, 서비스 완성 이후 필요가 없다고 판단되면 제거할 예정

   public ResponseEntity<?> getFile(@RequestParam String filename) {
   if (!awss3Service.doesObjectExist(filename)) {
   return ResponseEntity.notFound().build();
   }

   String url = awss3Service.getFileUrl(filename);
   return ResponseEntity.ok(url);
   }
   */

}
