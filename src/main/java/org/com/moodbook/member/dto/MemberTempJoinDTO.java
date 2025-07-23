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
  private String contact;
  private String gender;
  private String address;
  private String nickname;


}
