package org.com.moodbook.emotion.repository;

import java.util.List;
import org.com.moodbook.common.constants.EmotionTag;
import org.com.moodbook.emotion.entity.MemberEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberEmotionRepository extends JpaRepository<MemberEmotion, Long> {

  // 특정 유저의 모든 감정 삭제하기
  @Modifying
  @Transactional
  @Query("DELETE " + "FROM MemberEmotion me " + "WHERE me.member.id = :memberId")
  void deleteAllByMemberId(@Param("memberId") Long memberId);

  // 특정 유저가 가진 모든 감정 리스트를 가져온다
  @Query("SELECT DISTINCT e.emotion " + "FROM MemberEmotion me " + "JOIN me.emotion e "
      + "WHERE me.member.id = :memberId")
  List<EmotionTag> findAllEmotionByMemberId(@Param("memberId") Long memberId);
}