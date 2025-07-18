package org.com.moodbook.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberTempJoinDTO;
import org.com.moodbook.member.service.MemberService;
import org.com.moodbook.security.authentication.service.AuthenticationService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Tag(name = "MemberController", description = "로그인과 회원가입을 담당합니다.")
@RequestMapping("/api/oauth")
public class MemberController {
  private final MemberService memberService;
  private final AuthenticationService authenticationService;

  /**
   * [POST] 임시 회원가입
   */
  @Operation(summary = "임시 회원가입", description = "이메일 인증 이전의 회원가입을 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "회원가입에 실패하였습니다.")
  })
  @PostMapping("/tempSignUp")
  public ResponseEntity<MemberDTO>  tempSignUp(@RequestBody MemberTempJoinDTO dto) {

    MemberDTO result = memberService.tempjoin(dto);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * [POST] 로그인
   */
  @Operation(summary = "로그인", description = "로그인을 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "로그인에 실패하였습니다.")
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDto){
    //
     LoginResponseDTO tokenResponse = memberService.login(loginDto);

    return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
  }

  @GetMapping("/me")
  public ResponseEntity<MemberDTO> getMyInfo(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = memberDetails.getId();
    MemberDTO dto = memberService.getMyInfo(memberId);
    return ResponseEntity.ok(dto);
  }


}
