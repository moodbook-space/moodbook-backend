package org.com.moodbook.admin.chat.controller;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.admin.chat.dto.AdminChatRoomDTO;
import org.com.moodbook.admin.chat.service.AdminChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class AdminChatRoomController {

  private final AdminChatRoomService adminChatService;


  //페이징 데이터 조회
  @GetMapping()
  public Page<AdminChatRoomDTO> getChatList(
      @RequestParam(defaultValue = "") String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size);
    if (query != null && !query.isBlank()) {
      return adminChatService.searchChats(query, pageable);
    }
    return adminChatService.getChatList(pageable);
  }

  //채팅방 삭제
  @DeleteMapping("/{chatId}")
  public ResponseEntity<String> deleteChat(@PathVariable Long chatId){
    adminChatService.deleteById(chatId);
    return ResponseEntity.ok("삭제 완료");

  }





}
