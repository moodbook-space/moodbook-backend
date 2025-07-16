package org.com.moodbook.admin.member.repository;

import org.com.moodbook.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMemberRepository extends JpaRepository<Member, Long> {

}
