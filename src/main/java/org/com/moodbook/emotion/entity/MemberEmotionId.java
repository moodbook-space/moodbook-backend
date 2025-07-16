package org.com.moodbook.emotion.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


/** 해당 엔티티는 복합 키로 사용하기 위한 엔티티입니다.
 * @Embeddable은 복합키로 해당 엔티티가 사용 가능함을 나타냅니다
 * Serializable은 JPA의 복합키가 필수로 상속해야 할 클래스입니다
 * equals, hashCode는 JPA의 식별자가 필수로 가져야 할 함수입니다.
 * */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmotionId implements Serializable {
  private Long memberId;
  private Long emotionId;

  /**
   * 두 MemberEmotionId 객체가 같은지 비교합니다.
   * JPA에서 복합 키를 사용할 때 객체 동등성 비교에 사용됩니다.
   * 같은 memberId와 emotionId를 가지면 동일한 키로 간주합니다.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MemberEmotionId)) return false;
    MemberEmotionId that = (MemberEmotionId) o;
    return Objects.equals(memberId, that.memberId) && Objects.equals(emotionId, that.emotionId);
  }

  /**
   * equals와 함께 사용되며, 객체의 고유한 해시 값을 생성합니다.
   * HashMap, HashSet 등 컬렉션에서 올바른 동작을 위해 필수입니다.
   * memberId와 emotionId의 조합으로 고유한 해시 값을 계산합니다.
   */
  @Override
  public int hashCode() {
    return Objects.hash(memberId, emotionId);
  }
}
