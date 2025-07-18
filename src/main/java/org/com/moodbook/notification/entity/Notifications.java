package org.com.moodbook.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import javax.management.NotificationListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.com.moodbook.common.constants.NotifyType;
import org.com.moodbook.member.entity.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Table(name = "notification")
@Entity
@EntityListeners(NotificationListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;                                    // Id

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;                              // 알림을 받을 유저 ID (알림 수신자)

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotifyType notifyType;                      // 알림 유형

    @Column(nullable = false)
    @Builder.Default
    private boolean isRead = false;                     // 읽음 여부

    @Column(name = "to_name", nullable = false)         // 알림 발신자
    private String toName;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime createdAt;                    // 알림 생성 날짜

    @Column(name = "content", nullable = false)
    private String content;                             // 알림 메세지

    @Column(name = "url", nullable = false)
    private String url;                                 // 해당 알림 url

    public void updateNotificationsReadStatus(boolean isRead) {
        this.isRead = isRead;
    }

}

