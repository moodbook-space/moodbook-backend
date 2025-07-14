package org.com.moodbook.member.controller;

import lombok.AllArgsConstructor;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.MemberTempJoinDto;
import org.com.moodbook.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class MemberController {
  private final MemberService memberService;

  /**
   * [POST] 임시 회원가입
   */
  @PostMapping("/tempSignUp")
  public ResponseEntity<MemberDTO>  tempSignUp(@RequestBody MemberTempJoinDto dto) {
    MemberDTO result = memberService.tempjoin(dto);
    return ResponseEntity.ok(result);
  }

}
