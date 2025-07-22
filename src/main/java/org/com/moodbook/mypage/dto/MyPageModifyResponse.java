package org.com.moodbook.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.entity.MemberProfile;

@Getter
@Builder
public class MyPageModifyResponse {

  private String myImage;
  private String name;
  private String nickname;
  private String email;
  private String contact;

  public static MyPageModifyResponse of(Member member, MemberProfile memberProfile) {
    return MyPageModifyResponse.builder()
        .myImage(memberProfile.getMyImage())
        .name(member.getName())
        .nickname(memberProfile.getNickname())
        .email(member.getEmail())
        .contact(member.getContact())
        .build();
  }
}
