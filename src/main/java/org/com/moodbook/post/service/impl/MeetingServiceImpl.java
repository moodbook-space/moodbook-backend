package org.com.moodbook.post.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.MeetingJoinStatus;
import org.com.moodbook.common.constants.MeetingType;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.dto.CreateMeetingRequest;
import org.com.moodbook.post.dto.MeetingDetailResponse;
import org.com.moodbook.post.dto.MeetingJoinDto;
import org.com.moodbook.post.dto.MeetingJoinResponseRequest;
import org.com.moodbook.post.dto.MeetingSummaryResponse;
import org.com.moodbook.post.dto.UpdateMeetingRequest;
import org.com.moodbook.post.entity.Meeting;
import org.com.moodbook.post.entity.MeetingMember;
import org.com.moodbook.post.entity.MoodTag;
import org.com.moodbook.post.repository.MeetingMemberRepository;
import org.com.moodbook.post.repository.MeetingRepository;
import org.com.moodbook.post.repository.MoodTagRepository;
import org.com.moodbook.post.service.MeetingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

  private final MeetingRepository meetingRepository;
  private final MemberRepository memberRepository;
  private final MoodTagRepository tagRepository;
  private final MeetingMemberRepository meetingMemberRepo;

  @Override
  public Long createMeeting(Long memberId, CreateMeetingRequest req) {
    // 호스트 검증
    Member host = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    // 태그 조회
    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    // MeetingType 변환
    MeetingType type;
    try {
      type = MeetingType.valueOf(req.getMeetingType());
    } catch (Exception e) {
      throw new BaseException(ErrorCode.INVALID_MEETING_TYPE);
    }

    // 엔티티 저장
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
    meetingRepository.save(meeting);

    // 호스트 자동 승인
    MeetingMember self = MeetingMember.builder()
        .meeting(meeting)
        .member(host)
        .status(MeetingJoinStatus.APPROVED)
        .joinedAt(meeting.getCreatedAt())
        .build();
    meetingMemberRepo.save(self);

    return meeting.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public MeetingDetailResponse getMeeting(Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

    int count = meetingMemberRepo.findByMeetingIdAndStatus(meetingId, MeetingJoinStatus.APPROVED)
        .size();

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
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MeetingSummaryResponse> getMeetings(Pageable pageable) {
    return meetingRepository.findAll(pageable)
        .map(m -> MeetingSummaryResponse.builder()
            .id(m.getId())
            .title(m.getTitle())
            .hostName(m.getMember().getName())
            .meetingType(m.getMeetingType().name())
            .startAt(m.getStartAt())
            .currentParticipants(
                meetingMemberRepo.findByMeetingIdAndStatus(m.getId(), MeetingJoinStatus.APPROVED)
                    .size()
            )
            .capacity(m.getCapacity())
            .viewCount(m.getViewCount())
            .likeCount(m.getLikeCount())
            .tags(m.getMoodTags().stream()
                .map(MoodTag::getName)
                .collect(Collectors.toList()))
            .createdAt(m.getCreatedAt())
            .build()
        );
  }

  @Override
  public void updateMeeting(Long memberId, Long meetingId, UpdateMeetingRequest req) {
    // 모임과 작성자 검증
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }

    // moodTags 업데이트
    List<MoodTag> tags = tagRepository.findAllById(req.getTagIds());
    if (tags.isEmpty()) {
      throw new BaseException(ErrorCode.TAG_NOT_FOUND);
    }

    // MeetingType 변환
    MeetingType type;
    try {
      type = MeetingType.valueOf(req.getMeetingType());
    } catch (Exception e) {
      throw new BaseException(ErrorCode.INVALID_MEETING_TYPE);
    }

    // 필드 수정
    meeting.setTitle(req.getTitle());
    meeting.setContent(req.getContent());
    meeting.setMeetingType(type);
    meeting.setStartAt(req.getStartAt());
    meeting.setEndAt(req.getEndAt());
    meeting.setCapacity(req.getCapacity());
    meeting.setLocation(req.getLocation());
    meeting.setMoodTags(tags);

    // 저장 (트랜잭션 커밋 시 자동 flush)
    meetingRepository.save(meeting);
  }

  @Override
  public void deleteMeeting(Long memberId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }
    meetingRepository.deleteById(meetingId);
    // @SQLDelete 에 의해 soft-delete 처리
  }

  @Override
  public void requestJoinMeeting(Long memberId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));

    if (meetingMemberRepo.existsByMeetingIdAndMemberId(meetingId, memberId)) {
      throw new BaseException(ErrorCode.ALREADY_EXIST_JOIN);
    }

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    MeetingMember req = MeetingMember.builder()
        .meeting(meeting)
        .member(member)
        .status(MeetingJoinStatus.PENDING)
        .build();
    meetingMemberRepo.save(req);
  }

  @Override
  public void respondToJoinRequest(Long hostId, Long meetingId, Long requestId, MeetingJoinResponseRequest reqDto) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(hostId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }

    MeetingMember req = meetingMemberRepo.findById(requestId)
        .orElseThrow(() -> new BaseException(ErrorCode.JOIN_REQUEST_NOT_FOUND));
    if (!req.getMeeting().getId().equals(meetingId)) {
      throw new BaseException(ErrorCode.INVALID_REQUEST);
    }

    String action = reqDto.getAction();
    if ("APPROVE".equalsIgnoreCase(action)) {
      req.setStatus(MeetingJoinStatus.APPROVED);
      req.setJoinedAt(LocalDateTime.now());
    } else if ("REJECT".equalsIgnoreCase(action)) {
      req.setStatus(MeetingJoinStatus.REJECTED);
    } else {
      throw new BaseException(ErrorCode.INVALID_ACTION);
    }
    meetingMemberRepo.save(req);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MeetingJoinDto> listJoinRequests(Long hostId, Long meetingId) {
    Meeting meeting = meetingRepository.findById(meetingId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEETING_NOT_FOUND));
    if (!meeting.getMember().getId().equals(hostId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }

    return meetingMemberRepo.findByMeetingIdAndStatus(meetingId, MeetingJoinStatus.PENDING)
        .stream()
        .map(r -> new MeetingJoinDto(
            r.getId(),
            r.getMember().getId(),
            r.getMember().getName(),
            r.getStatus().name(),
            r.getCreatedAt().toString()
        ))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MeetingSummaryResponse> getMyMeetings(Long memberId, String role, Pageable pageable) {
    if ("participant".equalsIgnoreCase(role)) {
      // 내가 참가된 모임만
      Page<MeetingMember> joins = meetingMemberRepo.findByMemberIdAndStatus(memberId, MeetingJoinStatus.APPROVED, pageable);
      return joins.map(j -> {
        Meeting m = j.getMeeting();
        int count = meetingMemberRepo.findByMeetingIdAndStatus(m.getId(), MeetingJoinStatus.APPROVED).size();
        return MeetingSummaryResponse.builder()
            .id(m.getId())
            .title(m.getTitle())
            .hostName(m.getMember().getName())
            .meetingType(m.getMeetingType().name())
            .startAt(m.getStartAt())
            .currentParticipants(count)
            .capacity(m.getCapacity())
            .viewCount(m.getViewCount())
            .likeCount(m.getLikeCount())
            .tags(m.getMoodTags().stream().map(MoodTag::getName).toList())
            .createdAt(m.getCreatedAt())
            .build();
      });
    } else {
      // role=host 부분
      return meetingRepository.findByMember_Id(memberId, pageable)
          .map(m -> {
            int count = meetingMemberRepo.findByMeetingIdAndStatus(m.getId(), MeetingJoinStatus.APPROVED).size();
            return MeetingSummaryResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .hostName(m.getMember().getName())
                .meetingType(m.getMeetingType().name())
                .startAt(m.getStartAt())
                .currentParticipants(count)
                .capacity(m.getCapacity())
                .viewCount(m.getViewCount())
                .likeCount(m.getLikeCount())
                .tags(m.getMoodTags().stream().map(MoodTag::getName).toList())
                .createdAt(m.getCreatedAt())
                .build();
          });
    }
  }

}
