package org.com.moodbook.member.repository;

import java.util.Optional;
import org.com.moodbook.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  //메서드 네임 규칙에 따른 추상 메서드
  boolean existsByEmail(String email);
  boolean existsByContact(String contact);

  //가입된 회원 중 로그인을 위한 이메일 대조
  Optional<Member> findByEmail(String email);

  @Query("SELECT m FROM Member m JOIN FETCH m.notifications WHERE m.id = :id")
  Optional<Member> findByIdWithNotifications(@Param("id") Long id);
}
