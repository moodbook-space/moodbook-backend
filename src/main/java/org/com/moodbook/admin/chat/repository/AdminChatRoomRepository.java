package org.com.moodbook.admin.chat.repository;

import org.com.moodbook.admin.chat.dto.AdminChatRoomDTO;
import org.com.moodbook.admin.chat.entity.AdminChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminChatRoomRepository extends JpaRepository<AdminChatRoom, Long> {

  @Query("SELECT new org.com.moodbook.admin.chat.dto.AdminChatRoomDTO(" +
      "c.id, c.participants, c.name, c.createdBy, c.description, c.createdAt) " +
      "FROM AdminChatRoom c " +
      "ORDER BY c.createdAt DESC")
  Page<AdminChatRoomDTO> findAllPaging(Pageable pageable);
  //페이징 적용 채팅방 리스트 불러오기


  @Query("SELECT new org.com.moodbook.admin.chat.dto.AdminChatRoomDTO(" +
      "c.id, c.participants, c.name, c.createdBy, c.description, c.createdAt) " +
      "FROM AdminChatRoom c " +
      "WHERE c.name LIKE %:query% OR c.createdBy LIKE %:query%")
  Page<AdminChatRoomDTO> findByNameOrCreatedBy(@Param("query") String query, Pageable pageable);
  //채팅방 검색 기능

}
