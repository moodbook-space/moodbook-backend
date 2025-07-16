package org.com.moodbook.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableUtil {

  /**
   * !! 게시글 목록 조회 정렬 방법 세가지 !!
   * views → viewCount DESC
   * likes → likeCount DESC
   * 기존방식 → createdAt DESC
   */
  public static Pageable of(int page, int size, String sortType) {
    Sort sort;
    switch (sortType.toLowerCase()) {
      case "views":
        sort = Sort.by("viewCount").descending();
        break;
      case "likes":
        sort = Sort.by("likeCount").descending();
        break;
      default:
        sort = Sort.by("createdAt").descending();
    }
    return PageRequest.of(page, size, sort);
  }
}
