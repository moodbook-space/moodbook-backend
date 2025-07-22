package org.com.moodbook.post.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.com.moodbook.post.entity.Report;
import org.com.moodbook.post.entity.MoodTag;
import org.com.moodbook.book.entity.Book;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "report")
@Setting(settingPath = "elasticsearch/report-settings.json")
@Mapping(mappingPath = "elasticsearch/report-mappings.json")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportEsDocument {

  @Id
  private Long id;

  private String title;
  private String content;
  private int viewCount;
  private int likeCount;
  private Long memberId;
  private List<String> moodTags;

  // Report 전용 필드
  private Long bookId;


  public static ReportEsDocument fromEntity(Report r)   {
    return ReportEsDocument.builder()
        .id(r.getId())
        .title(r.getTitle())
        .content(r.getContent())
        .viewCount(r.getViewCount())
        .likeCount(r.getLikeCount())
        .memberId(r.getMember().getId())
        .moodTags(
            r.getMoodTags().stream()
                .map(MoodTag::getName)
                .toList()
        )
        .bookId(r.getBook().getId() != null ? r.getBook().getId() : null)
        .build();
  }
}
