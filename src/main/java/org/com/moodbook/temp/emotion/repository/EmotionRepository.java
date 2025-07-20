//package org.com.moodbook.temp.emotion.repository;
//
//import org.com.moodbook.common.constants.EmotionTag;
//import org.com.moodbook.temp.emotion.entity.Emotion;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface EmotionRepository extends JpaRepository<Emotion, Long> {
//  Boolean existsByEmotion(EmotionTag emotion);
//
//  @Query("SELECT e.id "
//      + "FROM Emotion e "
//      + "WHERE e.emotion = :emotion")
//  Long findIdByEmotion(@Param("emotion") EmotionTag emotion);
//}
