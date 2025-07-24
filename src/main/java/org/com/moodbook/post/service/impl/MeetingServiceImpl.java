package org.com.moodbook.post.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.bookchat.entity.ChatRoomMemberStatus;
import org.com.moodbook.bookchat.repository.ChatRoomMemberRepository;
import org.com.moodbook.common.constants.MeetingType;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.dto.ChatLinkRequest;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.com.moodbook.post.entity.Meeting;
import org.com.moodbook.post.entity.MoodTag;
import org.com.moodbook.post.repository.MeetingRepository;
import org.com.moodbook.post.repository.MoodTagRepository;
import org.com.moodbook.post.search.service.MeetingEsService;
import org.com.moodbook.post.service.LikeService;
import org.com.moodbook.post.service.MeetingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

  private final MeetingRepository meetingRepository;
  private final MemberRepository memberRepository;
  private final MoodTagRepository tagRepository;
  private final ChatRoomMemberRepository chatRoomMemberRepo;
  private final LikeService likeService;
  private final MeetingEsService meetingEsService;

  @Override
  public Long createMeeting(Long memberId, CreateMeetingRequest req) {
    Member host = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    MeetingType type;
    try {
      type = MeetingType.valueOf(req.getMeetingType());
    } catch (Exception e) {
      throw new BaseException(ErrorCode.INVALID_MEETING_TYPE);
    }

    Meeting meeting = Meeting.builder()
        .title(req.getTitle())
        .content(req.getContent())
        .member(host)
        .meetingType(type)
        .startAt(req.getStartAt())
        .endAt(req.getEndAt())
        .capacity(req.getCapacity())
        .location(req.getLocation())
        .moodTags(tags)
        .build();
    Meeting meetings = meetingRepository.save(meeting);

    meetingEsService.indexMeeting(meetings);

    return meeting.getId();
  }

  @Override
  @Transactional
  public MeetingDetailResponse getMeeting(Long memberId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

    int count = (int) chatRoomMemberRepo.countByChatRoom_IdAndStatus(
        meeting.getChatRoomId(), ChatRoomMemberStatus.APPROVED);

    meeting.setViewCount(meeting.getViewCount() + 1);
    meetingRepository.save(meeting);

    boolean liked = likeService.isLikedBy(memberId, meetingId);

    return MeetingDetailResponse.builder()
        .id(meeting.getId())
        .title(meeting.getTitle())
        .content(meeting.getContent())
        .meetingType(meeting.getMeetingType().name())
        .startAt(meeting.getStartAt())
        .endAt(meeting.getEndAt())
        .capacity(meeting.getCapacity())
        .location(meeting.getLocation())
        .viewCount(meeting.getViewCount())
        .likeCount(meeting.getLikeCount())
        .currentParticipants(count)
        .tags(meeting.getMoodTags().stream()
            .map(MoodTag::getName)
            .collect(Collectors.toList()))
        .hostName(meeting.getMember().getName())
        .createdAt(meeting.getCreatedAt())
        .updatedAt(meeting.getUpdatedAt())
        .likedByMe(liked)
        .chatRoomId(meeting.getChatRoomId())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MeetingSummaryResponse> getMeetings(Long memberId, Pageable pageable) {
    return meetingRepository.findAll(pageable)
        .map(m -> MeetingSummaryResponse.builder()
            .id(m.getId())
            .title(m.getTitle())
            .hostName(m.getMember().getName())
            .meetingType(m.getMeetingType().name())
            .startAt(m.getStartAt())
            .currentParticipants((int) chatRoomMemberRepo.countByChatRoom_IdAndStatus(
                m.getChatRoomId(), ChatRoomMemberStatus.APPROVED))
            .capacity(m.getCapacity())
            .viewCount(m.getViewCount())
            .likeCount(m.getLikeCount())
            .tags(m.getMoodTags().stream()
                .map(MoodTag::getName)
                .collect(Collectors.toList()))
            .createdAt(m.getCreatedAt())
            .likedByMe(likeService.isLikedBy(memberId, m.getId()))
            .chatRoomId(m.getChatRoomId())
            .build());
  }

  @Override
  public void updateMeeting(Long memberId, Long meetingId, UpdateMeetingRequest req) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }

    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    MeetingType type;
    try {
      type = MeetingType.valueOf(req.getMeetingType());
    } catch (Exception e) {
      throw new BaseException(ErrorCode.INVALID_MEETING_TYPE);
    }

    meeting.setTitle(req.getTitle());
    meeting.setContent(req.getContent());
    meeting.setMeetingType(type);
    meeting.setStartAt(req.getStartAt());
    meeting.setEndAt(req.getEndAt());
    meeting.setCapacity(req.getCapacity());
    meeting.setLocation(req.getLocation());
    meeting.setMoodTags(tags);

    meetingRepository.save(meeting);
    meetingEsService.indexMeeting(meeting);
  }

  @Override
  public void deleteMeeting(Long memberId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }
    meetingRepository.deleteById(meetingId);
    meetingEsService.deleteMeeting(meetingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MeetingSummaryResponse> getMyMeetings(Long memberId, Pageable pageable) {
    return meetingRepository.findByMember_Id(memberId, pageable)
        .map(m -> MeetingSummaryResponse.builder()
            .id(m.getId())
            .title(m.getTitle())
            .hostName(m.getMember().getName())
            .meetingType(m.getMeetingType().name())
            .startAt(m.getStartAt())
            .currentParticipants((int) chatRoomMemberRepo.countByChatRoom_IdAndStatus(
                m.getChatRoomId(), ChatRoomMemberStatus.APPROVED))
            .capacity(m.getCapacity())
            .viewCount(m.getViewCount())
            .likeCount(m.getLikeCount())
            .tags(m.getMoodTags().stream().map(MoodTag::getName).toList())
            .createdAt(m.getCreatedAt())
            .chatRoomId(m.getChatRoomId())
            .likedByMe(likeService.isLikedBy(memberId, m.getId()))
            .build());
  }

  @Override
  public void linkChatRoom(Long hostId, Long meetingId, ChatLinkRequest req) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(hostId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }
    if (meetingRepository.existsByChatRoomId(req.getChatRoomId())) {
      throw new BaseException(ErrorCode.CHAT_ROOM_ALREADY_LINKED);
    }
    meeting.setChatRoomId(req.getChatRoomId());
    meetingRepository.save(meeting);
  }
}
