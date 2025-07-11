package org.com.moodbook.post.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.com.moodbook.common.constants.MeetingType;

@Entity
@DiscriminatorValue("MEETING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Meeting extends BasePost {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MeetingType meetingType;

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  @Column(nullable = false)
  private int capacity;

  @Column(length = 200)
  private String location;


  @Column(name = "chat_room_id", length = 100)
  private String chatRoomId;
}

