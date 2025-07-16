package org.com.moodbook.admin.chat.repository;

import org.com.moodbook.admin.chat.dto.AdminChatRoomDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.com.moodbook.bookchat.entity.ChatRoom;

@Repository
public interface AdminChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  @Query("SELECT new org.com.moodbook.admin.chat.dto.AdminChatRoomDTO(" +
      "c.id, c.title, c.limitMembers, p.nickname, c.status, c.createdAt, " +
      "(SELECT COUNT(m) FROM c.members m)) " +
      "FROM ChatRoom c " +
      "JOIN MemberProfile p ON p.member = c.owner " +
      "ORDER BY c.createdAt DESC")
  Page<AdminChatRoomDTO> findAllPaging(Pageable pageable);

  //페이징 적용 채팅방 리스트 불러오기


  @Query("SELECT new org.com.moodbook.admin.chat.dto.AdminChatRoomDTO(" +
      "c.id, c.title, c.limitMembers, p.nickname, c.status, c.createdAt, " +
      "(SELECT COUNT(m) FROM c.members m)) " +
      "FROM ChatRoom c " +
      "JOIN MemberProfile p ON p.member = c.owner " +
      "WHERE c.title LIKE %:query% OR p.nickname LIKE %:query% " +
      "ORDER BY c.createdAt DESC")
  Page<AdminChatRoomDTO> findByNameOrCreatedBy(@Param("query") String query, Pageable pageable);

  //채팅방 검색 기능

}
