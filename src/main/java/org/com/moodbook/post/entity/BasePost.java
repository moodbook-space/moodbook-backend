package org.com.moodbook.post.entity;

import java.util.List;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.common.model.BaseTime;
import org.com.moodbook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * BasePost: 공통 게시글 엔티티
 * SINGLE_TABLE 상속 전략 사용, 소프트 삭제 및 감정 태그 매핑 처리
 */
@Entity
@Table(name = "post")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type", discriminatorType = DiscriminatorType.STRING)
@SQLDelete(sql = "UPDATE post SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BasePost extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private int viewCount = 0;

  @Column(nullable = false)
  private int likeCount = 0;

  /** 소프트 삭제 플래그 */
  @Column(nullable = false)
  private boolean deleted = false;

  /** 작성자(member) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  /** 감정 태그 매핑 */
  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "post_mood_tag",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private List<MoodTag> moodTags;
}

