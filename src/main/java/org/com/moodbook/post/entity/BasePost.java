package org.com.moodbook.post.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.common.model.BaseTime;
import org.com.moodbook.member.entity.Member;

/**
 * BasePost: 공통 게시글 엔티티
 * SINGLE_TABLE 상속 전략 사용, 소프트 삭제 및 감정 태그 매핑 처리
 */
@Entity
@Table(name = "post")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type", discriminatorType = DiscriminatorType.STRING)
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

  @OneToMany(mappedBy = "post",
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "post",
      cascade = CascadeType.REMOVE,
      orphanRemoval = true)
  private List<PostLike> likes = new ArrayList<>();

}

