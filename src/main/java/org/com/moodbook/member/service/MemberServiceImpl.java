package org.com.moodbook.member.service;


import static org.com.moodbook.common.exception.ErrorCode.EMAIL_NOT_VERIFIED;
import static org.com.moodbook.common.exception.ErrorCode.INVALID_PASSWORD;
import static org.com.moodbook.common.exception.ErrorCode.MEMBER_DEACTIVATED;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.AWSS3Constants;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberTempJoinDTO;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.security.authentication.repository.AuthenticationRepository;
import org.com.moodbook.security.authentication.service.AuthenticationService;
import org.com.moodbook.security.authentication.service.EmailAuthenticationService;
import org.com.moodbook.security.jwt.JwtProperties;
import org.com.moodbook.security.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

  //리포지토리 주입
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final AuthenticationService authenticationService;
  private final EmailAuthenticationService emailAuthenticationService;
  private final AuthenticationRepository authenticationRepository;
  private final RedisTemplate<String, String> redisTemplate;

  private Member findMemberOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
  }

  //임시 회원가입 진행
  @Override
  @Transactional
  public MemberDTO tempjoin(
      MemberTempJoinDTO dto) {

    if (memberRepository.existsByEmail(dto.getEmail())) {
      throw new BaseException(ErrorCode.ALREADY_EXIST_EMAIL);
    }
    if (memberRepository.existsByContact(dto.getContact())) {
      throw new BaseException(ErrorCode.ALREADY_EXIST_CONTACT);
    }


    Gender gender = Gender.valueOf(dto.getGender());


    Member member = Member.builder()
        .email(dto.getEmail())
        .role(Role.USER)
        .password(passwordEncoder.encode(dto.getPassword()))
        .status(MemberStatus.ACTIVATED)
        .name(dto.getName())
        .contact(dto.getContact())
        .build();

    MemberProfile profile = MemberProfile.builder()
        .gender(gender)
        .address(dto.getAddress())
        .myImage(AWSS3Constants.DEFAULT_PROFILE_IMAGE)
        .nickname(dto.getNickname())
        .build();

    member.setProfile(profile);
    memberRepository.save(member);
    emailAuthenticationService.sendEmail(member.getEmail());

    return MemberDTO.toDto(member);
  }


  //로그인
  @Override
  public LoginResponseDTO login(LoginRequestDTO dto) {
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
    // 5.모든 로그인 검증완료 -> 토큰 발급 및 저장 로직 호출
    MemberDTO memberDTO = MemberDTO.toDto(member);
    return authenticationService.issueTokenAndStore(memberDTO);
  }

  //회원가입

  // 내정보 가지고오기
  @Override
  @Transactional(readOnly = true)
  public MemberDTO getMyInfo(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    return MemberDTO.toDto(member);
  }


  @Override
  public void logout(Long requesterId, Long targetId) {

    //요청자
    Member requester = findMemberOrThrow(requesterId);
    //로그아웃 대상자
    Member target = findMemberOrThrow(targetId);

    // 요청자가 본인이 아닌데 아니라면 예외 처리
    if (!requester.getId().equals(target.getId())) {
      throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    //인증 정보 삭제(로그 아웃)->authentication에 저장 된 refresh토큰 삭제
    authenticationRepository.deleteByMember_Id(targetId);

    //요청 헤더에서 현재 AccessToken추출
    String accessToken = jwtTokenProvider.getCurrentToken();
    //토큰 만료 시간 계산
    long remainingTime = jwtTokenProvider.getAccessTokenRemainingTime(accessToken);

    //블랙리스트 저장 (남은 시간만큼 유효하게)
    redisTemplate.opsForValue().set(
        "access-token-blacklist:" + accessToken,
        "logout",
        Duration.ofMillis(remainingTime)
    );


  }

  @Override
  public void deactivate(Long requestId, Long targetId) {

    // 1.탈퇴 대상 사용자 조회
    Member deactivateMember = findMemberOrThrow(requestId);

    // 2.요청자 조회
    Member requester =  findMemberOrThrow(targetId);

    // 3.요청자가 관리자 이거나 또는 본인인지 확인
    if (!requester.getId().equals(deactivateMember.getId()) && requester.getRole() != Role.ADMIN) {
      throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS);

    }
    if (deactivateMember.getStatus() == MemberStatus.DEACTIVATED) {
      throw new BaseException(ErrorCode.MEMBER_ALREADY_DEACTIVATED);
    }

    // 5.상태 비활성화 처리
    deactivateMember.setStatus(MemberStatus.DEACTIVATED);
  }


}
