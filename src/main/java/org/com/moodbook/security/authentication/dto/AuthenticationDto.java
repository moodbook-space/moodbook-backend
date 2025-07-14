package org.com.moodbook.security.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.security.authentication.entity.Authentication;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDto {


  private Long id;
  private Member member;
  private String token;
  private String refreshToken;
  private String tokenType;




  //Entity를 DTO로 변환하는 메서드
  public static AuthenticationDto toDto(Authentication entity) {
    return AuthenticationDto.builder()
        .id(entity.getId())
        .member(entity.getMember())
        .token(entity.getToken())
        .refreshToken(entity.getRefreshToken())
        .tokenType(entity.getTokenType())
        .build();
  }

  //DTO를 Entity로 변환하는 메서드
  public Authentication toEntity() {
    return Authentication.builder()
        .id(id)
        .member(member)
        .token(token)
        .refreshToken(refreshToken)
        .tokenType(tokenType)
        .build();
  }


}
