package org.com.moodbook.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
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
  @Column(nullable = true)
  private MeetingType meetingType;

  @Column(nullable = true)
  private LocalDateTime startAt;

  @Column(nullable = true)
  private LocalDateTime endAt;

  @Column(nullable = true)
  private Integer capacity;

  @Column(length = 200)
  private String location;


  @Column(name = "chat_room_id", length = 100)
  private String chatRoomId;
}

