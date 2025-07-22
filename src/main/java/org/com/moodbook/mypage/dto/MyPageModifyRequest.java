package org.com.moodbook.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageModifyRequest {

  private String name;
  private String password;
  private String nickname;
  private String contact;
  private String address;
}
