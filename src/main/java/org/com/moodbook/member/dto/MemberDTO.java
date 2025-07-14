package org.com.moodbook.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.member.entity.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class MemberDTO {

  private Long id;
  private String email;
  private String password;
  private String name;
  private Role role;
  private String contact;
  private boolean emailVerified;
  private MemberStatus status;


  public Member toEntity() {
    return Member.builder()
        .id(id)
        .email(email)
        .password(password)
        .name(name)
        .role(role)
        .contact(contact)
        .status(status)
        .emailVerified(emailVerified)
        .build();
  }

  public static MemberDTO toDto(Member entity) {
    return MemberDTO.builder()
        .id(entity.getId())
        .email(entity.getEmail())
        .password(entity.getPassword())
        .name(entity.getName())
        .role(entity.getRole())
        .contact(entity.getContact())
        .status(entity.getStatus())
        .emailVerified(entity.isEmailVerified())
        .build();
  }
}


