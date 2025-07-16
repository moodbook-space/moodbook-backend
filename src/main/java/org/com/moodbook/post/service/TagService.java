package org.com.moodbook.post.service;

import java.util.List;
import org.com.moodbook.post.dto.TagResponse;

public interface TagService {
  /** 모든 감정 태그 조회 */
  List<TagResponse> getAllTags();
}
