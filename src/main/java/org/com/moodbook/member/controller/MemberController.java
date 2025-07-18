package org.com.moodbook.member.controller;

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
@RequestMapping("/api/oauth")
public class MemberController {
  private final MemberService memberService;
  private final AuthenticationService authenticationService;

  /**
   * [POST] 임시 회원가입
   */
  @PostMapping("/tempSignUp")
  public ResponseEntity<MemberDTO>  tempSignUp(@RequestBody MemberTempJoinDTO dto) {

    MemberDTO result = memberService.tempjoin(dto);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  /**
   * [POST] 로그인
   */
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
