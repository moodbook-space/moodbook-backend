package org.com.moodbook.admin.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminChatRoom {

  /** 에러 방지용 임시 엔티티 **/


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long participants;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String createdBy;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private LocalDateTime createdAt;


}
