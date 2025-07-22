package org.com.moodbook.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.mypage.dto.MyPageModifyRequest;
import org.com.moodbook.mypage.dto.MyPageModifyResponse;
import org.com.moodbook.mypage.dto.MyPageResponse;
import org.com.moodbook.mypage.service.MyPageService;
import org.com.moodbook.security.core.CustomMemberDetails;
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

  // 북마크 리스트 가져오기
  @GetMapping("/profile")
  @Operation(summary = "마이페이지 필요한 정보 출력", description = "닉네임, 프로필사진, 기분 리스트를 전달한다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "북마크 리스트 로드에 성공했습니다."),
      @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없습니다.")
  })
  public ResponseEntity<MyPageResponse> getMyPageInfo(
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {

    MyPageResponse myPageResponse = myPageService.getMyPageInfo(memberDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(myPageResponse);
  }

  @GetMapping("/modify")
  @Operation(summary = "개인정보 수정 페이지에 필요한 데이터 조회", description = "이름, 성별, 주소, 닉네임 등 개인정보 페이지에서 기본으로 띄워질 정보들을 제공합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "유저 정보 조회에 성공하였습니다."),
      @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없습니다.")
  })
  public ResponseEntity<MyPageModifyResponse> getMyPageModifyInfo(
      @AuthenticationPrincipal CustomMemberDetails memberDetails
  ) {

    MyPageModifyResponse response = myPageService.getMyPageModifyInfo(memberDetails.getId());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/modify/image")
  @Operation(summary = "프로필 사진 변경", description = "프로필 사진 변경 시에 호출되는 api, 데이터 전송 시에 multipart/form-data로 전송해야 함")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 사진 변경에 성공하였습니다."),
      @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없습니다.")
  })
  public ResponseEntity<MyPageModifyResponse> updateMyImage(
      @RequestParam("image") MultipartFile image,
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {

    MyPageModifyResponse response = myPageService.updateMyImage(memberDetails.getId(), image);
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  @PatchMapping("/modify")
  @Operation(summary = "개인정보 수정 완료 버튼을 눌렀을 때 실행되는 API", description = "개인정보를 받아 수정을 처리하고, 새 정보를 반환한다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "프로필 사진 변경에 성공하였습니다."),
      @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없습니다.")
  })
  public ResponseEntity<MyPageModifyResponse> updateMyPageInfo(
      @RequestBody MyPageModifyRequest mypageModifyRequest,
      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {

    MyPageModifyResponse myPageModifyResponse = myPageService.updateMyPageInfo(
        customMemberDetails.getId(), mypageModifyRequest);

    return ResponseEntity.status(HttpStatus.OK).body(myPageModifyResponse);
  }
}
