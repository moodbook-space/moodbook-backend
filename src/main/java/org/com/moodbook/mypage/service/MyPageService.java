package org.com.moodbook.mypage.service;

import org.com.moodbook.mypage.dto.MyPageResponse;
import org.com.moodbook.mypage.dto.UpdateNicknameDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {

  // 마이페이지에 띄워야 할 기본정보(닉네임, 프로필사진, 기분리스트) 불러오기
  MyPageResponse getMyPageInfo(Long memberId);

  // 내정보 업데이트하기, 현재는 닉네임만 업데이트할 수 있음.
  MyPageResponse updateMyPageInfo(Long memberId, UpdateNicknameDTO updateNicknameDTO);

  // 프로필사진 업데이트하기
  MyPageResponse updateMyImage(Long memberId, MultipartFile image);
}
