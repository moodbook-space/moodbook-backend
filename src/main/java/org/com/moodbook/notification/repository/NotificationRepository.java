package org.com.moodbook.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.com.moodbook.notification.entity.Notifications;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    /* N + 1 문제 처리 */
    @EntityGraph(attributePaths = {"member", "member.memberProfile"})
    List<Notifications> findByMemberIdOrderByCreatedAtDesc(Long memberId);

}
