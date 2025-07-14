package org.com.moodbook.security.authentication.repository;

import org.com.moodbook.security.authentication.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationReposiroty extends JpaRepository<Authentication, Long> {

}
