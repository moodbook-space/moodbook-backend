package org.com.moodbook.member.service;

import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberTempJoinDTO;


public interface MemberService {


  //임시 회원가입
  MemberDTO tempjoin(MemberTempJoinDTO memberTempJoinDto);

  //로그인
  LoginResponseDTO login(LoginRequestDTO memberLoginDTO);

  // 내 정보 가지고 오기
  MemberDTO getMyInfo(Long memberId);



}
