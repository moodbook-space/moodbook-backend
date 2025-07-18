package org.com.moodbook.common.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailUtil {


  private final JavaMailSender mailSender;


  /**
   * 인증 이메일 전송
   * @param toEmail 수신자 이메일
   * @param subject 제목
   * @param text HTML 형식의 본문 내용
   */
  public void sendEmail(String toEmail,String subject,String text){
    try{
      //1. MimeMessage 객체 생성 (이메일 메세지 틀을 만듦)
      // JavaMailSender가 제공하는 메서드로 실제 이메일 객체를 초기화함
      MimeMessage message = mailSender.createMimeMessage();

      //2. MimeMessageHelper를 사용해 메세지에 정보 설정 (내용,제목,수신자 등)
      //두 번째 인자: multipart 사용여부(false면 단순 메시지, 세 번째 인자: 문자 인코딩 지정)
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
      // 3. 수신자 이메일 주소 설정
      helper.setTo(toEmail);
      //4. 메일 제목 설정
      helper.setSubject(subject);
      //5.메일 본문 설정(HTML 사용 가능하도록 true 설정)
      helper.setText(text, true);//true는 HTML 형식

      //6. 작성한 메일 전송
      mailSender.send(message);

    }catch (MessagingException e){
      //7. 이메일 전송 중 오류 발생 시 예외 처리
      //예외 메세지를 포함하여 런타임 예외로 전환
      throw new RuntimeException("이메일 전송에 실패했습니다: "+ e.getMessage());
    }


  }

}
