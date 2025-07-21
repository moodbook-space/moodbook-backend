package org.com.moodbook.security.authentication.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.security.authentication.service.AuthenticationService;
import org.com.moodbook.security.authentication.service.EmailAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final EmailAuthenticationService emailAuthenticationService;


  //이메일 인증 api
  @GetMapping("/verify-email")
  public ResponseEntity<String> verifyEmail(@RequestParam String token) {

      emailAuthenticationService.verifyToken(token);//서비스에서 검증 + 상태 변경
      return ResponseEntity.status(HttpStatus.OK).body("이메일 인증이 완료 되었습니다");
  }

  //access토큰 만료 시 토큰 재발급 메서드
  @PostMapping("/reissue")
  public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String refreshToken) {

    String newAccessToken = authenticationService.reIssueAccessToken(refreshToken);

    Map<String, String> response = Map.of(
        "accessToken", newAccessToken,
        "tokenType","Bearer"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
