package org.com.moodbook.oauth2.dto;

import java.util.Map;
import lombok.RequiredArgsConstructor;

public class GoogleUserInfo implements OAuth2UserInfo {

  private final Map<String, Object> attributes;

  //생성자 주입
  public GoogleUserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  //구글은 email을 바로 최상위 키로 제공

  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getName() {
    return (String) attributes.get("name");
  }

  @Override
  public String getNickname() {
    return (String) attributes.get("nickname");
  }

  @Override
  public String getProfileImage() {
    return (String) attributes.get("profile_image");
  }

  @Override
  public String getId() {
    // 구글은 email이 이미 고유 식별자 역할을 하기 때문에 따로 ID를 사용하지 않음
    return "";
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }
}
