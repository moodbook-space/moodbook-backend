package org.com.moodbook.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mood_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 태그명 (예: 감동적인, 철학적인 등)
   */
  @Column(nullable = false, unique = true, length = 50)
  private String name;
}