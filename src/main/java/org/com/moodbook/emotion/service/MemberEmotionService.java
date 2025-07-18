package org.com.moodbook.emotion.service;

import org.com.moodbook.emotion.dto.MemberEmotionDTO;

// 멤버와 그 감정에 관련된 서비스 함수를 정의할 인터페이스
public interface MemberEmotionService {

  // 유저에 감정을 추가한다.
  MemberEmotionDTO addEmotionsToMember(Long memberId, MemberEmotionDTO request);

  // 유저의 감정 리스트 불러오기
  MemberEmotionDTO getEmotions(Long memberId);
}
