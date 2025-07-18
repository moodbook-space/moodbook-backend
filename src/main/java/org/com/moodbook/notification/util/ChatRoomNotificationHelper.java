package org.com.moodbook.notification.util;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.entity.ChatRoom;
import org.com.moodbook.bookchat.entity.ChatRoomMember;
import org.com.moodbook.bookchat.repository.ChatRoomMemberRepository;
import org.com.moodbook.common.constants.NotifyType;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.notification.service.NotificationService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRoomNotificationHelper {

    private final NotificationService notificationService;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // 가입 신청 알림
    public void notifyJoinRequest(ChatRoom chatRoom, Member member) {
        ChatRoomMember leader = chatRoomMemberRepository.findLeaderByChatRoomId(chatRoom.getId())
            .orElseThrow(() -> new BaseException(ErrorCode.LEADER_NOT_FOUND));

        String content = member.getName() + "님이 " + chatRoom.getTitle() + " 에 가입 신청을 하였습니다.";
        String url = "/api/chat-rooms" + chatRoom.getId() + "/join";

        notificationService.notifyMoodBookClient(
            leader.getMember(),
            NotifyType.CHAT_APPLY,
            content,
            url,
            leader.getMember().getName()
        );
    }

    // 승인 알림
    public void notifyApproval(ChatRoomMember chatRoomMember) {
        String content = chatRoomMember.getMember().getName() + "님의 " +
            chatRoomMember.getChatRoom().getTitle() + " 의 가입 신청이 완료되었습니다.";
        String url = "/api/chatRoom/" + chatRoomMember.getChatRoom().getId() + "/members/"
            + chatRoomMember.getId();

        notificationService.notifyMoodBookClient(
            chatRoomMember.getMember(),
            NotifyType.CHAT_APPROVAL,
            content,
            url,
            chatRoomMember.getMember().getName()
        );
    }

    // 거절 알림
    public void notifyRejection(ChatRoomMember chatRoomMember) {
        String content = chatRoomMember.getMember().getName() + "님의 " +
            chatRoomMember.getChatRoom().getTitle() + " 가입 신청이 거절되었습니다.";
        String url = "/api/chatRoom/" + chatRoomMember.getChatRoom().getId() + "/members/"
            + chatRoomMember.getId();

        notificationService.notifyMoodBookClient(
            chatRoomMember.getMember(),
            NotifyType.CHAT_REJECTED,
            content,
            url,
            chatRoomMember.getMember().getName()
        );
    }

}
