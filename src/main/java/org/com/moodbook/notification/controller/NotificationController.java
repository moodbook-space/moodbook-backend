package org.com.moodbook.notification.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.notification.dto.NotificationResponse;
import org.com.moodbook.notification.dto.UpdateNotificationRequest;
import org.com.moodbook.notification.dto.UpdateNotificationResponse;
import org.com.moodbook.notification.service.NotificationService;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api/notification")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    /** 구독 확인 **/
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomMemberDetails memberDetails,
        @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(memberDetails, lastEventId));
    }

    /** 사용자별 알림 조회 **/
    @GetMapping("/{memberId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable("memberId") Long memberId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(memberId);
        return ResponseEntity.ok(notifications);
    }

    /** 사용자별 알림 수정 **/
    @PatchMapping("/{notificationId}")
    public ResponseEntity<UpdateNotificationResponse> updateNotifications(
        @PathVariable("notificationId") Long notificationId,
        @AuthenticationPrincipal CustomMemberDetails memberDetails,
        @RequestBody UpdateNotificationRequest updateNotificationRequest) {
        UpdateNotificationResponse updateNotificationResponse =
            notificationService.updateNotificationReadStatus(notificationId, memberDetails, updateNotificationRequest);
        return ResponseEntity.ok(updateNotificationResponse);
    }

    /** 사용자별 알림 삭제 **/
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> deleteNotifications(
        @PathVariable("notificationId") Long notificationId,
        @AuthenticationPrincipal CustomMemberDetails memberDetails) {
        notificationService.deleteNotificationById(notificationId, memberDetails);
        return ResponseEntity.noContent().build();
    }

}
