package org.com.moodbook.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.common.constants.MemberStatus;
import org.com.moodbook.common.constants.Role;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberTempJoinDTO {

  private String email;
  private String password;
  private String name;
  private Role role;
  private String contact;
  private boolean emailVerified;
  private MemberStatus status;
  private Gender gender;
  private String address;
  private String myImage;
  private String nickname;


}
