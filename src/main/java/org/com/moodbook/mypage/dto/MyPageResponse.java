package org.com.moodbook.mypage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.member.entity.MemberProfile;

@Getter
@Builder
public class MyPageResponse {

  private String nickname;
  private String myImage;
  private List<EmotionTag> emotions;

  // MemberProfile, Emotion의 List 받아서 MyPageResponse 만들기
  public static MyPageResponse of(MemberProfile memberProfile, List<EmotionTag> emotions) {
    return MyPageResponse.builder()
        .nickname(memberProfile.getNickname())
        .myImage(memberProfile.getMyImage())
        .emotions(emotions)
        .build();
  }
}