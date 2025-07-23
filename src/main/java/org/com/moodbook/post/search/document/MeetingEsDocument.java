package org.com.moodbook.post.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.time.ZoneId;
import lombok.*;
import org.com.moodbook.post.entity.Meeting;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "meeting")
@Setting(settingPath = "elasticsearch/meeting-settings.json")
@Mapping(mappingPath = "elasticsearch/meeting-mappings.json")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingEsDocument {

  @Id
  private Long id;

  private String title;
  private String content;
  private int viewCount;
  private int likeCount;
  private Long memberId;
  private List<String> moodTags;

  // Meeting 전용 필드
  private String meetingType;
  private Instant startAt;
  private Instant endAt;
  private Integer capacity;
  private String location;
  private Long chatRoomId;

  public static MeetingEsDocument fromEntity(Meeting m) {
    return MeetingEsDocument.builder()
        .id(m.getId())
        .title(m.getTitle())
        .content(m.getContent())
        .viewCount(m.getViewCount())
        .likeCount(m.getLikeCount())
        .memberId(m.getMember().getId())
        .moodTags(
            m.getMoodTags().stream()
                .map(tag -> tag.getName())  // MoodTag 엔티티에 getName()이 있다고 가정
                .toList()
        )
        .meetingType(m.getMeetingType().name())
        .startAt(m.getStartAt().atZone(ZoneId.systemDefault()).toInstant())
        .endAt(m.getEndAt().atZone(ZoneId.systemDefault()).toInstant())
        .capacity(m.getCapacity())
        .location(m.getLocation())
        .chatRoomId(m.getChatRoomId())
        .build();
  }
}
