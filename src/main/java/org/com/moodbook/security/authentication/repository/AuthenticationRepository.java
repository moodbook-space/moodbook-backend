package org.com.moodbook.security.authentication.repository;

import java.util.Optional;
import org.com.moodbook.security.authentication.entity.AuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<AuthenticationEntity, Long> {

  Optional<AuthenticationEntity> findByMember_Id(Long memberId);
  void deleteByMember_Id(Long memberId);
}
