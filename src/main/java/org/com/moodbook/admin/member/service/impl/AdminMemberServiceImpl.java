package org.com.moodbook.admin.member.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.admin.member.dto.AdminMemberDTO;
import org.com.moodbook.admin.member.repository.AdminMemberProfileRepository;
import org.com.moodbook.admin.member.repository.AdminMemberRepository;
import org.com.moodbook.admin.member.service.AdminMemberService;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminMemberServiceImpl implements AdminMemberService {

  private final AdminMemberProfileRepository adminMemberProfileRepository;
  private final AdminMemberRepository adminMemberRepository;



  @Transactional(readOnly = true)
  @Override
  public Page<AdminMemberDTO> getMemberList(Pageable pageable) {

    return adminMemberProfileRepository.findAllPaging(pageable);
  }


  @Transactional(readOnly = true)
  @Override
  public Page<AdminMemberDTO> searchChats(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      return adminMemberProfileRepository.findAllPaging(pageable); // 기존 전체 목록
    }
    return adminMemberProfileRepository.findByNameOrCreatedBy(query, pageable);
  }


  @Override
  public void deleteMember(Long memberId) {
    if (!adminMemberProfileRepository.existsById(memberId)) {
      throw new BaseException(ErrorCode.MEMBER_NOT_FOUND);
    }
    adminMemberProfileRepository.deleteById(memberId);
    adminMemberRepository.deleteById(memberId);
  }


  public void updateMember(Long memberId, AdminMemberDTO dto) {
    MemberProfile profile = adminMemberProfileRepository.findByMemberId(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    Member member = profile.getMember();

    // MemberProfile 수정
    profile.setGender(dto.getGender());
    profile.setAddress(dto.getAddress());
    profile.setMyImage(dto.getMyImage());
    profile.setNickname(dto.getNickname());
    // Member 수정
    member.setEmail(dto.getEmail());
    member.setName(dto.getName());
    member.setContact(dto.getContact());

    // save() 호출 없이 트랜잭션 커밋 시 자동 반영 (더티 체킹)
  }


  @Override
  public AdminMemberDTO getMemberDetail(Long memberId) {
    return adminMemberProfileRepository.findByMemberId_DTO(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
