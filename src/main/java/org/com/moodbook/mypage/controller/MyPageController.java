package org.com.moodbook.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.mypage.dto.MyPageResponse;
import org.com.moodbook.mypage.dto.UpdateNicknameDTO;
import org.com.moodbook.mypage.service.MyPageService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.com.moodbook.security.core.CustomUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "MypageController", description = "마이페이지에서 사용되는 컨트롤러")
public class MyPageController {

  private final MyPageService myPageService;
  private final CustomUserDetailsService customUserDetailsService;

  // 북마크 리스트 가져오기
  @GetMapping("/profile")
  @Operation(summary = "프로필 출력에 필요한 정보 수집", description = "닉네임, 프로필사진, 기분 리스트를 전달한다.")
  @ApiResponses()
  public ResponseEntity<MyPageResponse> getMyPageInfo(
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {

    MyPageResponse myPageResponse = myPageService.getMyPageInfo(memberDetails.getId());
    return ResponseEntity.ok(myPageResponse);
  }

  @PatchMapping("/profile")
  @Operation(summary = "닉네임 변경", description = "닉네임 변경 시에 호출되는 api")
  public ResponseEntity<MyPageResponse> updateMyPageInfo(
      @RequestBody UpdateNicknameDTO updateNicknameDTO,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {

    MyPageResponse myPageResponse = myPageService.updateMyPageInfo(customMemberDetails.getId(),
        updateNicknameDTO);
    return ResponseEntity.status(HttpStatus.OK).body(myPageResponse);
  }

  @PatchMapping("/image")
  @Operation(summary = "프로필 사진 변경", description = "프로필 사진 변경 시에 호출되는 api, 데이터 전송 시에 multipart/form-data로 전송해야 함")
  public ResponseEntity<MyPageResponse> updateMyImage(
      @RequestParam("image") MultipartFile image,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {

    MyPageResponse response = myPageService.updateMyImage(memberDetails.getId(), image);
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }
}
