package org.com.moodbook.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.common.constants.NotifyType;
import org.com.moodbook.notification.entity.Notifications;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private NotifyType notifyType;
    private String toName;
    private String content;
    private boolean isRead;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String url;

    public static NotificationResponse fromEntity(Notifications notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .notifyType(notification.getNotifyType())
            .toName(notification.getToName())
            .content(notification.getContent())
            .createdAt(notification.getCreatedAt())
            .isRead(notification.isRead())
            .url(notification.getUrl())
            .build();
    }

}
