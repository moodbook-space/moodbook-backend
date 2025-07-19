package org.com.moodbook.mypage.service;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.awss3.dto.AWSS3DTO;
import org.com.moodbook.awss3.service.AWSS3Service;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.mypage.dto.MyPageResponse;
import org.com.moodbook.mypage.dto.UpdateNicknameDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService {

  private final MemberRepository memberRepository;
  private final AWSS3Service awsS3Service;

  @Override
  @Transactional(readOnly = true)
  // MemberId를 기반으로 MyPage에 출력할 값들을 반환한다.
  public MyPageResponse getMyPageInfo(Long memberId) {

    // 멤버가 없으면 에러 발생
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.MEMBER_NOT_FOUND);

    // 멤버 기반으로 memberProfile 가져와서 필요한 정보 채우기
    return MyPageResponse.of(member.getMemberProfile());
  }

  // 닉네임 업데이트에 사용해야 할 함수
  @Override
  public MyPageResponse updateMyPageInfo(Long memberId, UpdateNicknameDTO updateNicknameDTO) {

    // 멤버가 없으면 에러 발생
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.MEMBER_NOT_FOUND);

    // JPA기반으로 업데이트 수행
    member.getMemberProfile().setNickname(updateNicknameDTO.getNickname());

    // 완료된 결과 반환
    return MyPageResponse.of(member.getMemberProfile());
  }

  @Override
  public MyPageResponse updateMyImage(Long memberId, MultipartFile image) {

    // 먼저, 멤버 찾고 경로 DTO형태로 만들기
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> BaseException.MEMBER_NOT_FOUND);
    MemberProfile memberProfile = member.getMemberProfile();
    AWSS3DTO awsS3DTO = AWSS3DTO.of(memberProfile.getMyImage());

    // 이미 올라간 파일이 존재하는지 확인하고, 삭제
    awsS3Service.doesObjectExist(awsS3DTO);
    awsS3Service.deleteFile(awsS3DTO);

    // 새 파일 업로드하고, 업로드된 경로 기반으로 MemberProfile 수정하기
    memberProfile.setMyImage(awsS3Service.uploadFile(image).getUrl());

    // 결과 반환하기
    return MyPageResponse.of(memberProfile);
  }
}
