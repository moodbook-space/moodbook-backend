package org.com.moodbook.member.service;

import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.MemberProfileDTO;
import org.com.moodbook.member.dto.MemberTempJoinDto;
import org.com.moodbook.member.entity.Member;
import org.springframework.stereotype.Service;


public interface MemberService {


  //임시 회원가입
  MemberDTO tempjoin(MemberTempJoinDto memberTempJoinDto);

  //회원가입
  public MemberDTO join(MemberDTO memberDTO, MemberProfileDTO profileDTO);


}
