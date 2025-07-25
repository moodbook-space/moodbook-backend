package org.com.moodbook.member.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.com.moodbook.common.constants.Gender;

@Data
@AllArgsConstructor
@Builder
public class MemberDTOForUpdate {
  private Long id;
  private Gender gender;
  private String address;
  private String myImage;
  private String nickname;
  private String contact;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String email;
  private String name;
}
