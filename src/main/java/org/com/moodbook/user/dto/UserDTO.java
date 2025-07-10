package org.com.moodbook.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Role;
import org.com.moodbook.common.constants.UserStatus;
import org.com.moodbook.user.entity.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

  private Long id;
  private String email;
  private String password;
  private String name;
  private Role role;
  private String contact;
  private boolean emailVerified;
  private UserStatus status;


  public User toEntity() {
    return User.builder()
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

  public static UserDTO toDto(User entity) {
    return UserDTO.builder()
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


