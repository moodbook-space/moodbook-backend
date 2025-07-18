package org.com.moodbook.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.common.constants.NotifyType;
import org.com.moodbook.notification.entity.Notifications;

@Getter
@Builder
public class UpdateNotificationResponse {
    private Long id;
    private NotifyType notifyType;
    private String toName;
    private String content;
    private boolean isRead;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String url;

    public static UpdateNotificationResponse fromEntity(Notifications notification) {
        return UpdateNotificationResponse.builder()
            .id(notification.getId())
            .notifyType(notification.getNotifyType())
            .toName(notification.getToName())
            .content(notification.getContent())
            .createdAt(notification.getCreatedAt())
            .url(notification.getUrl())
            .isRead(notification.isRead())
            .build();
    }
}
