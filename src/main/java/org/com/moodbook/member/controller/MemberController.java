package org.com.moodbook.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.com.moodbook.common.util.AWSS3Uploader;
import org.com.moodbook.member.dto.LoginResponseDTO;
import org.com.moodbook.member.dto.MemberDTO;
import org.com.moodbook.member.dto.LoginRequestDTO;
import org.com.moodbook.member.dto.MemberDTOForUpdate;
import org.com.moodbook.member.dto.MemberTempJoinDTO;
import org.com.moodbook.member.service.MemberService;
import org.com.moodbook.security.authentication.service.AuthenticationService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@Tag(name = "MemberController", description = "로그인과 회원가입을 담당합니다.")
@RequestMapping("/api/oauth")
public class MemberController {

  private final MemberService memberService;
  private final AuthenticationService authenticationService;
  private final AWSS3Uploader awss3Uploader;

  /**
   * [POST] 임시 회원가입
   */
  @Operation(summary = "임시 회원가입", description = "이메일 인증 이전의 회원가입을 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "회원가입에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "회원가입에 실패하였습니다.")
  })
  @PostMapping("/tempSignUp")
  public ResponseEntity<String> tempSignUp(@RequestBody MemberTempJoinDTO dto) {

    MemberDTO result = memberService.tempjoin(dto);
    return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료 되었습니다.\n 이메일 인증 후 로그인해주세요" );
  }

  /**
   * [POST] 로그인
   */
  @Operation(summary = "로그인", description = "로그인을 진행합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "로그인에 성공하였습니다."),
      @ApiResponse(responseCode = "500", description = "로그인에 실패하였습니다.")
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDto) {
    //
    LoginResponseDTO tokenResponse = memberService.login(loginDto);

    return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
  }


  /**
   * 로그아웃 (본인 or 관리자)
   *
   * @param memberDetails 현재 로그인한 사용자 정보
   * @param targetId      로그아웃 대상 ID (선택)
   */
  //로그 아웃
  @Operation(summary = "로그아웃", description = "회원 본인 또는 관리자가 특정 사용자를 로그아웃함")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "로그 아웃 성공"),
      @ApiResponse(responseCode = "403", description = "권한이 없습니다"),
      @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않습니다")
  })
  @PostMapping("/logout/{targetId}")
  public ResponseEntity<String> logout(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @PathVariable("targetId") Long targetId) {
    Long requestId = memberDetails.getId();//로그인한 사용자 아이디
    Long logoutId = (targetId != null) ? targetId : requestId;

    memberService.logout(requestId, logoutId);

    return ResponseEntity.status(HttpStatus.OK).body("로그아웃 되었습니다");
  }

  @Operation(summary = "멤버 아이디 조회", description = "")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "조회에 성공했습니다."),
      @ApiResponse(responseCode = "403", description = "권한이 없습니다")
  })
  @GetMapping("/me")
  public ResponseEntity<MemberDTO> getMyInfo(
      @AuthenticationPrincipal CustomMemberDetails memberDetails) {
    Long memberId = memberDetails.getId();
    MemberDTO dto = memberService.getMyInfo(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(dto);
  }

  @Operation(summary = "회원 탈퇴(비활성화)",description = "MemberStatus를 deactivated로 변경")
  @ApiResponses({
      @ApiResponse(responseCode = "200",description = "탈퇴가 완료 되었습니다"),
      @ApiResponse(responseCode = "403",description = "권한이 없습니다"),
      @ApiResponse(responseCode = "404",description = "탈퇴할 회원이 존재하지 않습니다"),
      @ApiResponse(responseCode = "409",description = "이미 탈퇴 처리 된 회원입니다")
  })
  @PatchMapping("/deactivate/{targetId}")
  public ResponseEntity<String> deactivateMember(
      @AuthenticationPrincipal CustomMemberDetails memberDetails,
      @PathVariable("targetId") Long targetId
  ){
    Long requestId = memberDetails.getId();//비활성화 진행하는 아이디값
    memberService.deactivate(requestId, targetId);

    return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴가 완료 되었습니다");


  }

  @Operation(summary = "(관리자용) 멤버 사진 S3서버에 업로드",
      description = "(관리자용) 멤버 사진 S3서버에 업로드 후 url 반환")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "S3에 사진 업로드 성공"),
      @ApiResponse(responseCode = "500", description = "S3에 사진 업로드 실패")
  })
  @PostMapping("/admin/upload-image")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile file) {
    String imageUrl = awss3Uploader.upload(file);  // 👉 아까 보여주신 메서드 호출

    Map<String, String> response = new HashMap<>();
    response.put("imageUrl", imageUrl);

    return ResponseEntity.ok(response);
  }


  @Operation(summary = "(관리자용) 멤버 검색 및 조회", description = "검색어와 페이지 정보로 멤버 목록을 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "멤버 목록 조회 성공"),
      @ApiResponse(responseCode = "500", description = "멤버 목록 조회 실패")
  })
  @GetMapping("/admin/member")
  public ResponseEntity<Page<MemberDTO>> getMembers(
      @RequestParam(defaultValue = "") String query,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    Page<MemberDTO> result = memberService.searchMembers(query, pageable);
    return ResponseEntity.ok(result);
  }



  @Operation(summary = "(관리자용) 멤버 정보 상세 조회",
      description = "(관리자용) 멤버와 그에 맞는 프로필 정보를 모두 상세 조회합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "멤버를 성공적으로 상세조회했습니다."),
      @ApiResponse(responseCode = "500", description = "멤버 상세조회에 실패했습니다.")
  })
  @GetMapping("/admin/{memberId}")
  public MemberDTOForUpdate getMemberDetail(@PathVariable Long memberId) {
    return memberService.getMemberDetail(memberId);
  }


  @PatchMapping("/admin/{memberId}")
  public ResponseEntity<Void> updateMember(@PathVariable Long memberId,
      @ModelAttribute MemberDTOForUpdate dto) {
    memberService.updateMember(memberId, dto);
    return ResponseEntity.ok().build();
  }






}
