package org.com.moodbook.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.Gender;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDTO {

  private Long id;
  private Member member;
  private Gender gender;
  private String address;
  private String myImage;
  private String nickname;


  //DTO를 Entity로 변환하는 메소드
  public MemberProfile toEntity() {
    return MemberProfile
        .builder()
        .id(member.getId())
        .member(member)
        .gender(gender)
        .address(address)
        .myImage(myImage)
        .nickname(nickname)
        .build();
  }

  //Entity를 DTO로 변환하는 메서드
  public static MemberProfileDTO toDTO(MemberProfile entity) {
    return MemberProfileDTO.builder()
        .id(entity.getMember().getId())
        .member(entity.getMember())
        .gender(entity.getGender())
        .address(entity.getAddress())
        .myImage(entity.getMyImage())
        .nickname(entity.getNickname())
        .build();
  }

}
