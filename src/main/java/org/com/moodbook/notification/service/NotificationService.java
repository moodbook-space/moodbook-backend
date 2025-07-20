package org.com.moodbook.notification.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.com.moodbook.common.constants.NotifyType;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.notification.dto.NotificationResponse;
import org.com.moodbook.notification.dto.UpdateNotificationRequest;
import org.com.moodbook.notification.dto.UpdateNotificationResponse;
import org.com.moodbook.notification.entity.Notifications;
import org.com.moodbook.notification.repository.EmitterRepository;
import org.com.moodbook.notification.repository.NotificationRepository;
import org.com.moodbook.security.core.CustomMemberDetails;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    // 기본 타임아웃 설정 - 연결 지속시간 1시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 클라이언트가 구독을 위해 호출하는 메서드
     * memberId - 구독하는 클라이언트 회원 아이디, SseEmitter - 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(CustomMemberDetails memberDetails, String lastEventId)  {
        /**
         * findAllEmitterStartWithByUsername(member.getEmail()) 정보와 일치해야 함!
         * 현재 memberDetails.getUsername() 는 Email로 되어 있음.
         */
        String emitterId = memberDetails.getUserEmail() + "_" + System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(emitterId);
        });

        sendToClient(emitter, emitterId, "EventStream Created. [memberId = " + memberDetails.getId() + "]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events
                = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberDetails.getId()));
            events.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("SSE")
                .data(data, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
            log.error("Failed to send data to client for emitter ID: {}. Error: {}", emitterId, exception.getMessage());
        }
    }

    private void sendToMoodBookClient(SseEmitter emitter, String emitterId,
        NotificationResponse notificationResponse) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .name("SSE")
                .data(notificationResponse, MediaType.APPLICATION_JSON));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(exception);
            log.error("Failed to send data to client for emitter ID: {}. Error: {}",
                emitterId, exception.getMessage());
        }
    }

    /** 사용자별 알림 조회 **/
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByMemberId(Long memberId) {
        List<Notifications> notifications = notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return notifications.stream().map(NotificationResponse::fromEntity).toList();
    }

    /** 알림 읽음 처리 **/
    @Transactional
    public UpdateNotificationResponse updateNotificationReadStatus(
        Long notificationId, CustomMemberDetails memberDetails, UpdateNotificationRequest updateNotificationRequest) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notifications.getMember().getId().equals(memberDetails.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        notifications.updateNotificationsReadStatus(updateNotificationRequest.isRead());
        return UpdateNotificationResponse.fromEntity(notifications);
    }

    /** 알림 삭제 **/
    @Transactional
    public void deleteNotificationById(Long notificationId, CustomMemberDetails memberDetails) {
        Notifications notifications = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notifications.getMember().getId().equals(memberDetails.getId())) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        notificationRepository.deleteById(notifications.getId());
    }

    /** MoodBook 서비스 사용자에게 알림 전달 **/
    public void notifyMoodBookClient(Member member, NotifyType notifyType, String content, String url, String toName) {
        Notifications notifications = notificationRepository.save(
            Objects.requireNonNull(createNotification(member, notifyType, content, url, toName)));

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUsername(member.getEmail());

        log.info(sseEmitters.toString());

        NotificationResponse notificationResponse = NotificationResponse.fromEntity(notifications);

        sseEmitters.forEach(
            (key, emitter) -> {
                emitterRepository.saveEventCache(key, notifications);
                sendToMoodBookClient(emitter, key, notificationResponse);
            }
        );
    }

    private Notifications createNotification(Member member, NotifyType notifyType,
        String content, String url, String toName) {
        if (notifyType.equals(NotifyType.CHAT_APPLY)) {                        // 스터디 가입 신청
            return Notifications.builder()
                .member(member)
                .isRead(false)
                .notifyType(NotifyType.CHAT_APPLY)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.CHAT_APPROVAL)) {              // 스터디 가입 승인
            return Notifications.builder()
                .member(member)
                .isRead(false)
                .notifyType(NotifyType.CHAT_APPROVAL)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else if (notifyType.equals(NotifyType.CHAT_REJECTED)) {              // 스터디 가입 승인
            return Notifications.builder()
                .member(member)
                .isRead(false)
                .notifyType(NotifyType.CHAT_REJECTED)
                .content(content)
                .url(url)
                .createdAt(LocalDateTime.now())
                .toName(toName)
                .build();
        } else {
            return null;
        }
    }

}
