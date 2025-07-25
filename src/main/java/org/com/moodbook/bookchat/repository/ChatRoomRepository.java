package org.com.moodbook.bookchat.repository;

import java.util.List;
import java.util.Optional;
import org.com.moodbook.bookchat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  @Query("SELECT r FROM ChatRoom r JOIN FETCH r.owner ORDER BY r.createdAt DESC")
  List<ChatRoom> findAllWithOwnerOrderByCreatedAtDesc();

  @Query("SELECT r FROM ChatRoom r JOIN FETCH r.owner WHERE r.id = :roomId")
  Optional<ChatRoom> findWithOwnerById(@Param("roomId") Long roomId);

  @Query("SELECT c FROM ChatRoom c JOIN FETCH c.owner " +
      "WHERE c.title LIKE %:query% OR c.owner.name LIKE %:query% " +
      "ORDER BY c.createdAt DESC")
  List<ChatRoom> findAllByTitleOrOwnerName(@Param("query") String query);
}
