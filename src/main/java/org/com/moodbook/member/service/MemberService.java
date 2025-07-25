package org.com.moodbook.member.service;

import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberTempJoinDTO;


public interface MemberService {


  //임시 회원가입
  MemberDTO join(MemberTempJoinDTO memberTempJoinDto);

  //로그인
  LoginResponseDTO login(LoginRequestDTO memberLoginDTO);

  // 내 정보 가지고 오기
  MemberDTO getMyInfo(Long memberId);

  //로그 아웃
  void logout(Long requestId,Long targetId);

  //회원탈퇴(soft delete)->status를 deactivated로 변경
  void deactivate(Long requestId,Long targetId);






}
