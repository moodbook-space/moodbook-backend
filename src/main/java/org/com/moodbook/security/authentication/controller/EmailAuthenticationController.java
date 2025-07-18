package org.com.moodbook.security.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.security.authentication.service.EmailAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailAuthenticationController {
  private final EmailAuthenticationService emailAuthenticationService;


  @GetMapping("/verify-email")
  public ResponseEntity<String> verifyEmail(@RequestParam String token) {
    emailAuthenticationService.verifyToken(token);//서비스에서 검증 + 상태 변경
    return ResponseEntity.status(HttpStatus.OK).body("이메일 인증이 완료 되었습니다");
  }
}
