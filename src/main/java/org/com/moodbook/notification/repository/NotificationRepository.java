package org.com.moodbook.notification.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.com.moodbook.notification.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    List<Notifications> findByMemberIdOrderByCreatedAtDesc(Long memberId);

}
