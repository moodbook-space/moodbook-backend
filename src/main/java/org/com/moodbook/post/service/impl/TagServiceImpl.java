package org.com.moodbook.post.service.impl;


import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.post.dto.TagResponse;
import org.com.moodbook.post.repository.MoodTagRepository;
import org.com.moodbook.post.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

  private final MoodTagRepository tagRepository;

  /**
   * 태그 선택에 보여줘야 하므로 모든 저장된 태그 조회 하는 로직
   */
  @Override
  public List<TagResponse> getAllTags() {
    return tagRepository.findAll().stream()
        .map(tag -> new TagResponse(tag.getId(), tag.getName()))
        .collect(Collectors.toList());
  }
}
