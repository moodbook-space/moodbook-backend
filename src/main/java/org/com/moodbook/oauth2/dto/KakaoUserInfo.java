package org.com.moodbook.oauth2.dto;

import java.util.Map;
import lombok.Setter;

@SuppressWarnings("unchecked")
public class KakaoUserInfo implements OAuth2UserInfo{
  private final Map<String, Object> attributes;
  @Setter
  private String email;

  public  KakaoUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }
  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }


  @Override
  public String getName() {
    return getNickname();
  }

  @Override
  public String getNickname() {
    try {
      Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
      Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
      return profile != null ? (String) profile.get("nickname") : null;
    }catch (Exception e) {
      return null;
    }
  }
  // 프로필 이미지 추출
  @Override
  public String getProfileImage() {
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    return (String) profile.get("profile_image_url");
  }

  @Override
  public String getId() {
    // 카카오 고유 ID는 attributes.get("id")에서 가져올 수 있음
    return String.valueOf(attributes.get("id"));//Long -> String 으로 형변환
  }

}

