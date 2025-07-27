package org.com.moodbook.security.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.config.AppUrlProperties;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.security.authentication.dto.PasswordResetRequestDto;
import org.com.moodbook.security.authentication.service.AuthenticationService;
import org.com.moodbook.security.authentication.service.EmailAuthenticationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
  private final AuthenticationService authenticationService;
  private final EmailAuthenticationService emailAuthenticationService;
  private final RedisTemplate<String, String> redisTemplate;
  private final AppUrlProperties appUrlProperties;


  //이메일 인증 api
  @Operation(summary = "회원가입 이메일 인증",description = "회원가입 진행 시 이메일 인증을 요청하는 메서드")
  @ApiResponses({
      @ApiResponse(responseCode = "200",description = "회원가입이 완료 되었습니다 인증 후 진행해주세요"),
      @ApiResponse(responseCode = "500",description = "알 수 없는 오류입니다")
  })
  @GetMapping("/verify-email")
  public ResponseEntity<String> verifyEmail(@RequestParam String token) {

      emailAuthenticationService.verifyToken(token);//서비스에서 검증 + 상태 변경
      return ResponseEntity.status(HttpStatus.OK).body("이메일 인증이 완료 되었습니다");
  }

  //access토큰 만료 시 토큰 재발급 메서드
  @Operation(summary = "액세스 토큰 재발급",description = "access토큰 만료 시 refresh토큰 검증 후 access토큰 재발급")
  @ApiResponses({
      @ApiResponse(responseCode = "201",description = "accerssToken 재발급 완료"),
      @ApiResponse(responseCode = "403",description = "refesh토큰 만료")
  })
  @PostMapping("/reissue")
  public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String refreshToken) {

    String newAccessToken = authenticationService.reIssueAccessToken(refreshToken);

    Map<String, String> response = Map.of(
        "accessToken", newAccessToken,
        "tokenType","Bearer"
    );

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
  @Operation(summary = "비밀번호 재설정 요청",description = "입력한 메일로 비밀번호 재설정 인증링크 발송")
  @ApiResponses({
      @ApiResponse(responseCode = "200",description = "인증메일이 발송 되었습니다"),
      @ApiResponse(responseCode = "404",description = "인증하려는 메일이 존재하지 않습니다")
  })
  @PostMapping("/reset-password/request")
  public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
    emailAuthenticationService.sendPasswordResetEmail(email);
    return ResponseEntity.status(HttpStatus.OK).body("비밀번호 재설정 링크가 이메일로 전송되었습니다. \n메일함을 확인해주세요.");
  }

  @Operation(summary = "비밀번호 재설정 인증",description = "링크 클릭 시 redis를 통한 비밀번호 재설정 인증확인")
  @ApiResponses({
      @ApiResponse(responseCode = "200",description = "인증 완료"),
      @ApiResponse(responseCode = "403",description = "인증 토큰 부적합")

  })
  @GetMapping("/reset-password/verify-token")
  public ResponseEntity<Void> redirectToPasswordReset(@RequestParam String token) {
    Boolean isValid = redisTemplate.hasKey("password-reset:"+token);
    if (isValid) {
      //프론트의 재설정 페이지로 리다이렉트 ex:React 비밀번호 재설정 페이지
      URI redirectUri = URI.create(appUrlProperties.getFrontend() + "auth/password-reset?token="+token);
      return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();//302리다이렉트
    }else {
      throw new BaseException(ErrorCode.INVALID_TOKEN);
    }


  }

  //비밀번호 재설정 완료 메서드
  @Operation(summary = "비밀번호 재설정",description = "사용자가 비밀번호 재설정하여 디비로 요청")
  @ApiResponses({
      @ApiResponse(responseCode = "200",description = "비밀번호 재설정이 완료 되었습니다"),
      @ApiResponse(responseCode = "403", description = "만료되었거나 잘못 된 토큰입니다")
  })
  @PostMapping("/reset-password/confirm")
  public ResponseEntity<?> confirmPasswordReset(@RequestBody PasswordResetRequestDto dto) {
    emailAuthenticationService.resetPassword(dto.getNewPassword(), dto.getToken());
    return ResponseEntity.status(HttpStatus.OK).body("비밀번호 재설정 완료");

  }




}
