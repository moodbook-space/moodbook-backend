package org.com.moodbook.oauth2.dto;

import java.util.Map;

//공통 소셜 사용자 정보 인터페이스
public interface OAuth2UserInfo {
  //원본 Attribute 전체 반환
  Map<String, Object> getAttributes();

  //사용자 이메일 반환
  //사용자 이름 반환
  String getName();
  //사용자 닉네임 반환
  String getNickname();
  //사용자 이미지 반환
  String getProfileImage();
  String getId();



}

