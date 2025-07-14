package org.com.moodbook.post.entity;

import org.com.moodbook.common.model.BaseTime;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.member.entity.Member;

/**
 * 댓글(Comment) 엔티티
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comment extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 댓글 내용 */
  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  /** 댓글이 달린 게시글 (BasePost 상속 게시글) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private BasePost post;

  /** 댓글 작성자 (Member) */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member author;
}