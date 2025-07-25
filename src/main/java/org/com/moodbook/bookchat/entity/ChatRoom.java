package org.com.moodbook.bookchat.entity;


import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import java.time.LocalDateTime;
import org.com.moodbook.common.model.BaseTime;
import org.com.moodbook.member.entity.Member;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private int limitMembers;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Member owner;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatRoomStatus status;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    if (this.status == null) {
      this.status = ChatRoomStatus.OPEN;
    }
  }

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChatRoomMember> members;


}
