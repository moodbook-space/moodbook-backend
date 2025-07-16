package org.com.moodbook.admin.member.service;

import org.com.moodbook.admin.member.dto.AdminMemberDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMemberService {


  /** 멤버 리스트 가져오기 */
  Page<AdminMemberDTO> getMemberList(Pageable pageable);

  /** 멤버 검색 */
  Page<AdminMemberDTO> searchChats(String query, Pageable pageable);

  /** 멤버 정보 상세 조회 */
  AdminMemberDTO getMemberDetail(Long memberId);

  /** 멤버 정보 업데이트 **/
  void updateMember(Long memberId, AdminMemberDTO dto);

  /** 멤버 탈퇴 */
  void deleteMember(Long memberId);
}
