package org.com.moodbook.member.service;

import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.MemberProfileDTO;
import org.com.moodbook.member.dto.MemberTempJoinDto;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberProfileRepository;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public MemberDTO tempjoin(MemberTempJoinDto dto) {

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
    MemberProfile profileEntity = profileDTO.toEntity();

    memberRepository.save(entity);
    return null;
  }

  //회원가입





}
