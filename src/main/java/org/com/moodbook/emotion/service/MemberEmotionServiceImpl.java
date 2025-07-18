package org.com.moodbook.emotion.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.emotion.dto.MemberEmotionDTO;
import org.com.moodbook.emotion.entity.MemberEmotion;
import org.com.moodbook.emotion.repository.EmotionRepository;
import org.com.moodbook.emotion.repository.MemberEmotionRepository;
import org.com.moodbook.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberEmotionServiceImpl implements MemberEmotionService {

  private final MemberRepository memberRepository;
  private final MemberEmotionRepository memberEmotionRepository;
  private final EmotionRepository emotionRepository;

  @Override
  @Transactional
  public MemberEmotionDTO addEmotionsToMember(Long memberId, MemberEmotionDTO request) {
    if (!memberRepository.findById(memberId).isPresent()) {
      throw BaseException.MEMBER_NOT_FOUND;
    }

    // 우선, 해당 유저가 가진 모든 감정 태그를 지운다.
    memberEmotionRepository.deleteAllByMemberId(memberId);

    List<EmotionTag> listEmotions = new ArrayList<>();
    // 전달된 request기반으로 값을 모두 추가해준다.
    for (EmotionTag tag : request.getEmotions()) {
      // 감정 텍스트 기반으로 키를 불러온다 (ex. 슬픔 -> 1, 기쁨 -> 2)
      Long emotionId = emotionRepository.findIdByEmotion(tag);

      // 불러온 값을 하나씩 넣는다.
      memberEmotionRepository.save(MemberEmotion.of(memberId, emotionId));

      listEmotions.add(tag);
    }

    // 삽입된 리스트를 반환한다.
    return MemberEmotionDTO.of(listEmotions);
  }

  @Override
  @Transactional(readOnly = true)
  public MemberEmotionDTO getEmotions(Long memberId) {

    return MemberEmotionDTO.of(memberEmotionRepository.findAllEmotionByMemberId(memberId));
  }
}
