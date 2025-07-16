package org.com.moodbook.security.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.security.authentication.entity.AuthenticationEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDto {


  private Long id;
  private Member member_id;
  private String token;
  private String refreshToken;
  private String tokenType;




  //Entity를 DTO로 변환하는 메서드
  public static AuthenticationDto toDto(AuthenticationEntity entity) {
    return AuthenticationDto.builder()
        .id(entity.getId())
        .member_id(entity.getMember_id())
        .token(entity.getToken())
        .refreshToken(entity.getRefreshToken())
        .tokenType(entity.getTokenType())
        .build();
  }

  //DTO를 Entity로 변환하는 메서드
  public AuthenticationEntity toEntity() {
    return AuthenticationEntity.builder()
        .id(id)
        .member_id(member_id)
        .token(token)
        .refreshToken(refreshToken)
        .tokenType(tokenType)
        .build();
  }


}
