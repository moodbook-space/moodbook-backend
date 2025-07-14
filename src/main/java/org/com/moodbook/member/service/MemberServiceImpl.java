package org.com.moodbook.member.service;


import static org.com.moodbook.common.exception.ErrorCode.EMAIL_NOT_VERIFIED;
import static org.com.moodbook.common.exception.ErrorCode.INVALID_PASSWORD;
import static org.com.moodbook.common.exception.ErrorCode.MEMBER_DEACTIVATED;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.MemberLoginDTO;
import org.com.moodbook.member.dto.MemberProfileDTO;
import org.com.moodbook.member.dto.MemberTempJoinDto;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberProfileRepository;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

  //리포지토리 주입
  private final MemberRepository memberRepository;
  private final MemberProfileRepository memberProfileRepository;
  private final PasswordEncoder passwordEncoder;

  //임시 회원가입 진행
  @Override
  @Transactional
  public MemberDTO tempjoin(
       MemberTempJoinDto dto) {

    if (memberRepository.existsByEmail(dto.getEmail())) {
      throw new BaseException(ErrorCode.ALREADY_EXIST_EMAIL);
    }
    if (memberRepository.existsByContact(dto.getContact())) {
      throw new BaseException(ErrorCode.ALREADY_EXIST_CONTACT);
    }

    Member member = Member.builder()
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .role(dto.getRole())
        .name(dto.getName())
        .contact(dto.getContact())
        .emailVerified(dto.isEmailVerified())
        .status(dto.getStatus())
        .build();

    MemberProfile profile = MemberProfile.builder()
        .gender(dto.getGender())
        .address(dto.getAddress())
        .myImage(dto.getMyImage())
        .nickname(dto.getNickname())
        .build();

    member.setProfile(profile);
    memberRepository.save(member);

    return MemberDTO.toDto(member);
  }

  @Override
  public MemberDTO join(MemberDTO memberDTO, MemberProfileDTO profileDTO) {

    Member entity = memberDTO.toEntity();
//    MemberProfile profileEntity = profileDTO.toEntity();

    memberRepository.save(entity);
    return null;
  }

  //로그인
  @Override
  public MemberDTO login(MemberLoginDTO dto) {
    // 1.이메일을 통한 회원 존재 여부 확인
    Member member = memberRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    // 2. 비밀번호 일치 여부 확인
    if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
      throw new BaseException(INVALID_PASSWORD);
    }

    // 3. 이메일 인증 여부 확인
    if (!member.isEmailVerified()) {
      throw new BaseException(EMAIL_NOT_VERIFIED);
    }

    // 4.활성화 상태 여부 확인
    if (member.getStatus() == MemberStatus.DEACTIVATED) {
      throw new BaseException(MEMBER_DEACTIVATED);
    }

    return MemberDTO.toDto(member);
  }

  //회원가입


}
