package org.com.moodbook.mypage.service;

import org.com.moodbook.mypage.dto.MyPageModifyRequest;
import org.com.moodbook.mypage.dto.MyPageModifyResponse;
import org.com.moodbook.mypage.dto.MyPageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {

  // 마이페이지에 띄워야 할 기본정보(닉네임, 프로필사진, 기분리스트) 불러오기
  MyPageResponse getMyPageInfo(Long memberId);

  // 내정보 수정 페이지에 띄워야 할 정보 불러오기
  MyPageModifyResponse getMyPageModifyInfo(Long memberId);

  // 내정보 업데이트하기.
  MyPageModifyResponse updateMyPageInfo(Long memberId, MyPageModifyRequest updateNicknameDTO);

  // 프로필사진 업데이트하기
  MyPageModifyResponse updateMyImage(Long memberId, MultipartFile image);
}
