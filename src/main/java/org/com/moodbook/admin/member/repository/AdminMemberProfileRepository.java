package org.com.moodbook.admin.member.repository;

import java.util.Optional;
import org.com.moodbook.admin.member.dto.AdminMemberDTO;
import org.com.moodbook.member.entity.MemberProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMemberProfileRepository extends JpaRepository<MemberProfile, Long> {

  @Query("SELECT new org.com.moodbook.admin.member.dto.AdminMemberDTO(" +
      "p.id, p.gender, p.address, p.myImage, p.nickname,m.contact, m.createdAt, m.updatedAt,m.email,m.name) " +
      "FROM MemberProfile p " +
      "JOIN p.member m " +
      "ORDER BY m.updatedAt DESC")
  Page<AdminMemberDTO> findAllPaging(Pageable pageable);
  //멤버 리스트로 뽑기


  @Query("SELECT new org.com.moodbook.admin.member.dto.AdminMemberDTO(" +
      "p.id, p.gender, p.address, p.myImage, p.nickname,m.contact, m.createdAt, m.updatedAt,m.email,m.name) "
      +
      "FROM MemberProfile p " +
      "JOIN p.member m " +
      "WHERE p.nickname LIKE %:query% OR p.address LIKE %:query% OR m.email LIKE %:query% OR m.name LIKE %:query%")
  Page<AdminMemberDTO> findByNameOrCreatedBy(@Param("query") String query, Pageable pageable);
  //멤버 검색해서 리스트로 뽑기

  @Query("SELECT new org.com.moodbook.admin.member.dto.AdminMemberDTO(" +
      "p.id, p.gender, p.address, p.myImage, p.nickname,m.contact, m.createdAt, m.updatedAt,m.email,m.name) " +
      "FROM MemberProfile p " +
      "JOIN p.member m " +
      "WHERE m.id = :memberId")
  Optional<AdminMemberDTO> findByMemberId_DTO(@Param("memberId") Long memberId);
  //멤버 하나의 상세 정보만 따옴

  @Query("SELECT p FROM MemberProfile p JOIN FETCH p.member WHERE p.id = :memberId")
  Optional<MemberProfile> findByMemberId(@Param("memberId") Long memberId);
  //엔티티 그대로 회원 정보를 반환하는 메서드
}