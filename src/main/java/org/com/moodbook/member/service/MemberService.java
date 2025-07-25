package org.com.moodbook.member.service;

import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberDTOForUpdate;
import org.com.moodbook.member.dto.MemberTempJoinDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MemberService {


  //임시 회원가입
  MemberDTO tempjoin(MemberTempJoinDTO memberTempJoinDto);

  //로그인
  LoginResponseDTO login(LoginRequestDTO memberLoginDTO);

  // 내 정보 가지고 오기
  MemberDTO getMyInfo(Long memberId);

  //로그 아웃
  void logout(Long requestId,Long targetId);

  //회원탈퇴(soft delete)->status를 deactivated로 변경
  void deactivate(Long requestId,Long targetId);







  /** (관리자용) 멤버 검색 */
  Page<MemberDTO> searchMembers(String query, Pageable pageable);

  /** (관리자용) 멤버 정보 업데이트 **/
  void updateMember(Long memberId, MemberDTOForUpdate dto);

  MemberDTOForUpdate getMemberDetail(Long memberId);


}
