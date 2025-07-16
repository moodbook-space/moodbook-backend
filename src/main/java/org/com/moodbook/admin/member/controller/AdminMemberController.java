package org.com.moodbook.admin.member.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.admin.member.dto.AdminMemberDTO;
import org.com.moodbook.admin.member.service.impl.AdminMemberServiceImpl;
import org.com.moodbook.awss3.service.AWSS3Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

  private final AdminMemberServiceImpl adminMemberService;
  private final AWSS3Service awss3Service;

  /** 🔹 1. 유저 리스트 조회 (검색 + 페이징 + 정렬) */
  @GetMapping("")
  public Page<AdminMemberDTO> getMemberList(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    if (query != null && !query.isBlank()) {
      return adminMemberService.searchChats(query, pageable);
    }
    return adminMemberService.getMemberList(pageable);
  }

  /** 🔹 2. 유저 상세 조회 (선택 시 우측에 표시) */
  @GetMapping("/{memberId}")
  public AdminMemberDTO getUserDetail(@PathVariable Long memberId) {
    return adminMemberService.getMemberDetail(memberId);
  }

  /** 🔹 3. 유저 정보 수정 */
  @PutMapping("/{memberId}")
  public void updateMember(@PathVariable Long memberId, @RequestBody AdminMemberDTO dto) {
    adminMemberService.updateMember(memberId, dto);
  }

  /** 🔹 4. 유저 삭제 (탈퇴) **/
  @DeleteMapping("/{memberId}")
  public void deleteMember(@PathVariable Long memberId) {
    adminMemberService.deleteMember(memberId);
  }

  /** 🔹 5. 이미지 S3 서버에 올리기 **/
  @PostMapping("/uploadImage")
  public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {
    String imageUrl = awss3Service.uploadFile(image);
    return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
  }
}
