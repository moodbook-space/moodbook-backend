package org.com.moodbook.admin.chat.service;

import org.com.moodbook.admin.chat.dto.AdminChatRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AdminChatRoomService {



  //채팅방 리스트 페이지로 불러오기
  Page<AdminChatRoomDTO> getChatList(Pageable pageable);

  //채팅방 삭제
  void deleteById(Long chatId);

  //채팅방 검색
  Page<AdminChatRoomDTO> searchChats(String query, Pageable pageable);


}
