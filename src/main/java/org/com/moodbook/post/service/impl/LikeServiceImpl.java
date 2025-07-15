// src/main/java/org/com/moodbook/post/service/impl/LikeServiceImpl.java
package org.com.moodbook.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.entity.PostLike;
import org.com.moodbook.post.repository.BasePostRepository;
import org.com.moodbook.post.repository.PostLikeRepository;
import org.com.moodbook.post.service.LikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

  private final PostLikeRepository likeRepo;
  private final BasePostRepository postRepo;
  private final MemberRepository memberRepo;

  @Override
  public void toggleLike(Long memberId, Long postId) {
    // 게시글 유무 확인
    var post = postRepo.findById(postId)
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    // 회원 존재 확인
    var member = memberRepo.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    // 좋아요 로직
    if (likeRepo.existsByPost_IdAndMember_Id(postId, memberId)) {
      likeRepo.deleteByPost_IdAndMember_Id(postId, memberId);
    } else {
      PostLike like = PostLike.builder()
          .post(post)
          .member(member)
          .build();
      likeRepo.save(like);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public long countLikes(Long postId) {

    postRepo.findById(postId)
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    return likeRepo.countByPost_Id(postId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isLikedBy(Long memberId, Long postId) {
    // 회원,게시글 검증
    postRepo.findById(postId)
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    memberRepo.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    return likeRepo.existsByPost_IdAndMember_Id(postId, memberId);
  }
}
