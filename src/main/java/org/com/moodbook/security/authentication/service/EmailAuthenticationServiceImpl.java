package org.com.moodbook.security.authentication.service;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.common.util.EmailUtil;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailAuthenticationServiceImpl implements EmailAuthenticationService {

  private final EmailUtil emailUtil;
  private final RedisTemplate<String, String> redisTemplate;
  private final MemberRepository memberRepository;

  @Transactional
  @Override
  public void sendEmail(String toEmail) {
    try {//1. 인증 토큰 생성
      String token = UUID.randomUUID().toString();
      //2. Redis에 토큰 저장(10분으로 TTL)
      redisTemplate.opsForValue().set(token, toEmail, Duration.ofMinutes(10));
      // 3. 이메일 제목 및 본문 구성
      String subject = "[MoodBook] 이메일 인증 요청";
      String body = "<h3>이메일 인증<h3><p>"
          + "아래 링크를 클릭하여 이메일 인증을 완료하세요.</p>"
          + "<a href='http://localhost:8080/email/verify-email?token=" + token + "'>이메일 인증하기</a>";

      // 4.유틸 호출로 실제 메일 발송
      emailUtil.sendEmail(toEmail, subject, body);
    }catch (Exception e){
      e.printStackTrace();
      throw new RuntimeException("이메일 전송 중 에러 발생",e);
    }

  }

  @Override
  @Transactional
  public void verifyToken(String token) {
    //1.Redis에서 토큰으로 이메일 조회
    String email = redisTemplate.opsForValue().get(token);
    if (email == null) {
      throw new BaseException(ErrorCode.INVALID_TOKEN);//유효하지 않거나 만료 된 토큰
    }
    //2.이메일로 회원 조회
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    //3.이메일 인증 상태 변경
    if (member.isEmailVerified()){
      throw new BaseException(ErrorCode.ALREADY_VERIFIED);//이미 인증이 된 경우
    }
    //모든 경로에 이상 없을 시 이메일 인증 상태를 true로 변경
    member.setEmailVerified(true);
    memberRepository.save(member);

    // 4.인증이 끝날 시 토큰 삭제

    redisTemplate.delete(token);
  }

}
