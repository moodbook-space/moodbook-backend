package org.com.moodbook.bookchat.service;

import static org.com.moodbook.common.constants.NotifyType.CHAT_APPLY;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.dto.ApproveJoinRequest;
import org.com.moodbook.bookchat.dto.ChatRoomMemberResponse;
import org.com.moodbook.bookchat.dto.ChatRoomResponse;
import org.com.moodbook.bookchat.dto.CreateChatRoomRequest;
import org.com.moodbook.bookchat.dto.UpdateChatRoomRequest;
import org.com.moodbook.bookchat.entity.ChatRoom;
import org.com.moodbook.bookchat.entity.ChatRoomMember;
import org.com.moodbook.bookchat.entity.ChatRoomMemberRole;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.entity.ChatRoomStatus;
import org.com.moodbook.bookchat.repository.ChatRoomMemberRepository;
import org.com.moodbook.bookchat.repository.ChatRoomRepository;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.notification.service.NotificationService;
import org.com.moodbook.notification.util.ChatRoomNotificationHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    private final NotificationService notificationService;
    private final ChatRoomNotificationHelper chatRoomNotificationHelper;

    @Override
    @Transactional
    public ChatRoomResponse createRoom(CreateChatRoomRequest request) {
        Long ownerId = request.getOwner().getMemberId();
        String ownerName = request.getOwner().getMemberName();

        Member owner = memberRepository.findById(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        ChatRoom chatRoom = ChatRoom.builder()
            .title(request.getTitle())
            .limitMembers(request.getLimitMembers())
            .owner(owner)
            .status(ChatRoomStatus.OPEN) // enum: OPEN, CLOSED 등
            .build();

        chatRoomRepository.save(chatRoom);

        ChatRoomMember leader = ChatRoomMember.builder()
            .chatRoom(chatRoom)
            .member(owner)
            .role(ChatRoomMemberRole.LEADER)
            .status(ChatRoomMemberStatus.APPROVED)
            .joinedAt(LocalDateTime.now())
            .build();

        chatRoomMemberRepository.save(leader);

        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getTitle(),
            chatRoom.getLimitMembers(),
            "OPEN",
            ownerId,
            ownerName,
            LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    public ChatRoomResponse updateRoomSettings(UpdateChatRoomRequest request) {
        Long chatRoomId = request.getChatRoomId();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new IllegalArgumentException("ChatRoom not found"));

        Long ownerId = request.getOwner().getMemberId();
        String ownerName = request.getOwner().getMemberName();

        if (!chatRoom.getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.CHAT_ROOM_MEMBER_NOT_LEADER);
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            chatRoom.setTitle(request.getTitle());
        }
        if (request.getLimitMembers() != 0) {
            chatRoom.setLimitMembers(request.getLimitMembers());
        }

        chatRoomRepository.save(chatRoom);

        return new ChatRoomResponse(
            chatRoom.getId(),
            chatRoom.getTitle(),
            chatRoom.getLimitMembers(),
            "OPEN",
            ownerId,
            ownerName,
            LocalDateTime.now()
        );
    }

    @Override
    public List<ChatRoomResponse> findAllRooms() {
        List<ChatRoom> rooms = chatRoomRepository.findAllWithOwnerOrderByCreatedAtDesc();
        return rooms.stream()
            .map(ChatRoomResponse::fromEntity)
            .toList();
    }

    @Override
    public ChatRoomResponse findRoomByRoomId(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findWithOwnerById(roomId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

        return ChatRoomResponse.fromEntity(chatRoom);
    }

    @Override
    @Transactional
    public ChatRoomMemberResponse requestJoinChatRoom(Long roomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

        boolean alreadyJoined = chatRoomMemberRepository.existsByChatRoomAndMember(chatRoom,
            member);
        if (alreadyJoined) {
            throw new BaseException(ErrorCode.ALREADY_EXIST_JOIN);
        }

        ChatRoomMember waitJoinMember = ChatRoomMember.builder()
            .chatRoom(chatRoom)
            .member(member)
            .role(ChatRoomMemberRole.WAITING)
            .status(ChatRoomMemberStatus.WAITING)
            .build();
        chatRoomMemberRepository.save(waitJoinMember);


        ChatRoomMember savedMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, member)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));


//        // 채팅방 멤버 가입 신청 알림
//        chatRoomNotificationHelper.notifyJoinRequest(chatRoom, member);

        System.out.println(member.getName());

        return ChatRoomMemberResponse.from(savedMember);
    }

    @Override
    @Transactional
    public ChatRoomMemberResponse approveJoinChatRoom(ApproveJoinRequest request) {

        Long chatRoomMemberId = request.getChatRoomMemberId();
        Long roomId = request.getRoomId();
        Long ownerId = request.getApproveId();
        boolean approve = request.isApprove();
        ChatRoomMember joinRequest = chatRoomMemberRepository.findByChatRoomIdAndMemberId(roomId, chatRoomMemberId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));
        Member owner = chatRoom.getOwner();
        System.out.println(chatRoomMemberId);
        Member member = memberRepository.findById(chatRoomMemberId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

        if (!owner.getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.CHAT_ROOM_MEMBER_NOT_LEADER);
        }

        if (approve) {
            joinRequest.setStatus(ChatRoomMemberStatus.APPROVED);
            joinRequest.setRole(ChatRoomMemberRole.MEMBER);

            ChatRoomMember saved = chatRoomMemberRepository.save(joinRequest);

            // 채팅방 멤버 가입 신청 알림
            chatRoomNotificationHelper.notifyJoinRequest(chatRoom, member);

            return ChatRoomMemberResponse.from(saved);
        } else {
            joinRequest.setStatus(ChatRoomMemberStatus.REJECTED);
            joinRequest.setRole(ChatRoomMemberRole.REJECTED);
            chatRoomMemberRepository.save(joinRequest);

            // 거절 알림 전송
            chatRoomNotificationHelper.notifyRejection(joinRequest);

            chatRoomMemberRepository.delete(joinRequest);

            return ChatRoomMemberResponse.from(joinRequest);
        }
    }

    @Override
    @Transactional
    public List<ChatRoomMemberResponse> getMembers(Long roomId, ChatRoomMemberStatus status) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

        List<ChatRoomMember> members = chatRoomMemberRepository.findByChatRoomAndStatus(chatRoom,
            status);

        return members.stream()
            .map(ChatRoomMemberResponse::from)
            .toList();
    }


    @Override
    public void deleteRoom(Long roomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

        if (!chatRoom.getOwner().getId().equals(memberId)) {
            throw new BaseException(ErrorCode.CHAT_ROOM_MEMBER_NOT_LEADER);
        }
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public void leaveRoom(Long chatRoomId, Long memberId) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findByChatRoomAndMember(chatRoom,
                member)
            .orElseThrow(() -> new BaseException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

        if (chatRoomMember.getRole() == ChatRoomMemberRole.LEADER) {
            throw new BaseException(ErrorCode.CHAT_ROOM_MEMBER_NOT_LEADER); // "방장은 직접 나갈 수 없습니다."
        }

        chatRoomMemberRepository.delete(chatRoomMember);
    }

    @Override
    public boolean isMember(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BaseException(ErrorCode.CHATROOM_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        return chatRoomMemberRepository.existsByChatRoomAndMember(chatRoom, member);
    }
}