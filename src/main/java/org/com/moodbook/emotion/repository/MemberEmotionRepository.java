package org.com.moodbook.emotion.repository;

import java.util.List;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.emotion.entity.MemberEmotion;
import org.com.moodbook.emotion.entity.MemberEmotionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberEmotionRepository extends JpaRepository<MemberEmotion, MemberEmotionId> {

  @Modifying
  @Transactional
  @Query("DELETE "
      + "FROM MemberEmotion em "
      + "WHERE em.member.id = :memberId")
    // 특정 유저번호가 가진 모든 감정 리스트를 지운다.
  void deleteAllByMemberId(@Param("memberId") Long memberId);

  // 특정 유저가 가진 모든 감정 리스트를 가져온다
  @Transactional
  @Query("SELECT e.emotion "
      + "FROM MemberEmotion em "
      + "JOIN em.emotion e "
      + "WHERE em.member.id = :memberId")
  List<EmotionTag> getAllEmotionByMemberId(@Param("memberId") Long memberId);
}