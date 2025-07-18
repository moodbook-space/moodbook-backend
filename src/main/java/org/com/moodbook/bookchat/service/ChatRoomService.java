package org.com.moodbook.bookchat.service;

import java.util.List;
import org.com.moodbook.bookchat.dto.ApproveJoinRequest;
import org.com.moodbook.bookchat.dto.ChatRoomMemberResponse;
import org.com.moodbook.bookchat.dto.ChatRoomResponse;
import org.com.moodbook.bookchat.dto.ChatRoomSearchRequest;
import org.com.moodbook.bookchat.dto.CreateChatRoomRequest;
import org.com.moodbook.bookchat.dto.UpdateChatRoomRequest;
import org.com.moodbook.bookchat.entity.ChatRoomMemberRole;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.entity.ChatRoom;

public interface ChatRoomService {

  // 채팅방 생성
  ChatRoomResponse createRoom(CreateChatRoomRequest request);

  // 채팅방 설정 변경
  ChatRoomResponse updateRoomSettings(UpdateChatRoomRequest request);

  // 채팅방 리스트 전체 조회
  List<ChatRoomResponse> findAllRooms();

  // 채팅방 상세 조회
  ChatRoomResponse findRoomByRoomId(Long roomId);

  // 채팅방 입장 신청
  ChatRoomMemberResponse requestJoinChatRoom(Long roomId, Long memberId);

  // 방장 : 입장 승인/거절
  ChatRoomMemberResponse approveJoinChatRoom(ApproveJoinRequest request);

  // 방 내 멤버/대기자 리스트 조회 (상태별 채팅 멤버 조회)
  List<ChatRoomMemberResponse> getMembers(Long roomId, ChatRoomMemberStatus status);

  // 채팅방 삭제
  void deleteRoom(Long roomId, Long memberId);

  // 방 나가기
  void leaveRoom(Long chatRoomId, Long memberId);
}
