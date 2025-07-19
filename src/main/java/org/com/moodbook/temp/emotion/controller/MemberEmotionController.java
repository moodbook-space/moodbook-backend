//package org.com.moodbook.temp.emotion.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.com.moodbook.security.core.CustomMemberDetails;
//import org.com.moodbook.temp.emotion.dto.MemberEmotionDTO;
//import org.com.moodbook.temp.emotion.service.MemberEmotionService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/emotion")
//@Tag(name = "MemberEmotionController", description = "사용자의 현재 감정 기반 기능을 제공하기 위한 컨트롤러")
//public class MemberEmotionController {
//
//  private final MemberEmotionService memberEmotionService;
//
//  @PostMapping
//  @Operation(summary = "사용자에게 감정 추가", description = "사용자가 원래 가지고있던 감정 리스트를 모두 삭제하고, 새로 들어온 감정 리스트를 추가한다.")
//  @ApiResponses({@ApiResponse(responseCode = "200", description = "감정 리스트 삭제 및 추가에 성공하였습니다."),
//      @ApiResponse(responseCode = "500", description = "감정 리스트 삭제 및 추가에 실패하였습니다.")})
//  public ResponseEntity<?> addEmotionsToMember(
//      @AuthenticationPrincipal CustomMemberDetails customMemberDetails,
//      @RequestBody MemberEmotionDTO request) {
//    MemberEmotionDTO memberEmotionDTO = memberEmotionService.addEmotionsToMember(
//        customMemberDetails.getId(), request);
//    return ResponseEntity.ok(memberEmotionDTO);
//  }
//
//  @GetMapping
//  @Operation(summary = "사용자의 감정 가져오기", description = "토큰의 사용자 정보를 기반으로 기분 리스트를 불러온다.")
//  @ApiResponses({
//      @ApiResponse(responseCode = "200", description = "토큰의 사용자 정보를 기반으로 기분 리스트를 불러왔습니다."),
//      @ApiResponse(responseCode = "500", description = "토큰의 사용자 정보를 기반으로 기분 리스트를 불러오는데 실패했습니다.")})
//  public ResponseEntity<?> getEmotions(
//      @AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
//    MemberEmotionDTO memberEmotionDTO = memberEmotionService.getEmotions(
//        customMemberDetails.getId());
//    return ResponseEntity.ok(memberEmotionDTO);
//  }
//
//}
