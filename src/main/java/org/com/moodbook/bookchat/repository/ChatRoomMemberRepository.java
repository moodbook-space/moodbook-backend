package org.com.moodbook.bookchat.repository;

import java.util.List;
import java.util.Optional;
import org.com.moodbook.bookchat.entity.ChatRoom;
import org.com.moodbook.bookchat.entity.ChatRoomMember;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.entity.ChatRoomStatus;
import org.com.moodbook.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

  Boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
  @Query("""
    SELECT m FROM ChatRoomMember m
    JOIN FETCH m.member
    WHERE m.chatRoom = :chatRoom AND m.status = :status
""")
  List<ChatRoomMember> findByChatRoomAndStatus(
      @Param("chatRoom") ChatRoom chatRoom,
      @Param("status") ChatRoomMemberStatus status
  );

  @Query("SELECT c FROM ChatRoomMember c JOIN FETCH c.member WHERE c.chatRoom = :chatRoom AND c.member = :member")
  Optional<ChatRoomMember> findByChatRoomAndMember(@Param("chatRoom") ChatRoom chatRoom, @Param("member") Member member);
         
  ChatRoomMember findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

  long countByChatRoom_IdAndStatus(Long chatRoomId, ChatRoomMemberStatus status);

  /* N + 1 문제 처리 */
  @Query("SELECT crm FROM ChatRoomMember crm "
      + "JOIN FETCH crm.member m "
      + "JOIN FETCH m.memberProfile "
      + "WHERE crm.chatRoom.id = :chatRoomId "
      + "AND crm.role = 'LEADER'")
  Optional<ChatRoomMember> findLeaderByChatRoomId(@Param("chatRoomId") Long chatRoomId);

  Long countByChatRoomAndStatus(ChatRoom chatRoom, ChatRoomMemberStatus status);

}




