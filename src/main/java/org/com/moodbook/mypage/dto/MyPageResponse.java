package org.com.moodbook.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.member.entity.MemberProfile;

@Getter
@Builder
public class MyPageResponse {

  private String nickname;
  private String myImage;

  // MemberProfile, Emotion의 List 받아서 MyPageResponse 만들기
  public static MyPageResponse of(MemberProfile memberProfile) {
    return MyPageResponse.builder().nickname(memberProfile.getNickname())
        .myImage(memberProfile.getMyImage()).build();
  }
}