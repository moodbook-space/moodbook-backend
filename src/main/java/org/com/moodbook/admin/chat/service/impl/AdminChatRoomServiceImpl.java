package org.com.moodbook.admin.chat.service.impl;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.admin.chat.dto.AdminChatRoomDTO;
import org.com.moodbook.admin.chat.repository.AdminChatRoomRepository;
import org.com.moodbook.admin.chat.service.AdminChatRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminChatRoomServiceImpl implements AdminChatRoomService {

  private final AdminChatRoomRepository adminChatRepository;



  //채팅방 리스트 페이지로 불러오기
  @Override
  public Page<AdminChatRoomDTO> getChatList(Pageable pageable) {
    return adminChatRepository.findAllPaging(pageable);
  }

  //채팅방 삭제
  public void deleteById(Long chatId){
    if(!adminChatRepository.existsById(chatId)){
      throw new NoSuchElementException("해당 ID의 채팅방이 존재하지 않습니다.");
    }
    adminChatRepository.deleteById(chatId);
  }

  //채팅방 검색
  @Override
  public Page<AdminChatRoomDTO> searchChats(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      return adminChatRepository.findAllPaging(pageable); // 기존 전체 목록
    }
    return adminChatRepository.findByNameOrCreatedBy(query, pageable);
  }

}
