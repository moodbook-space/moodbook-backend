package org.com.moodbook.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.user.entity.User;
import org.com.moodbook.user.entity.UserProfile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

  private Long id;
  private User user;
  private Gender gender;
  private String address;
  private String myImage;
  private String nickname;


  //DTO를 Entity로 변환하는 메소드
  public UserProfile toEntity() {
    return UserProfile
        .builder()
        .id(user.getId())
        .user(user)
        .gender(gender)
        .address(address)
        .myImage(myImage)
        .nickname(nickname)
        .build();
  }

  //Entity를 DTO로 변환하는 메서드
  public static UserProfileDTO toDTO(UserProfile entity) {
    return UserProfileDTO.builder()
        .id(entity.getUser().getId())
        .user(entity.getUser())
        .gender(entity.getGender())
        .address(entity.getAddress())
        .myImage(entity.getMyImage())
        .nickname(entity.getNickname())
        .build();
  }

}
