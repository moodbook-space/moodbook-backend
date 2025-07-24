package org.com.moodbook.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.entity.Member;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.entity.BasePost;
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

    BasePost post = postRepo.findById(postId)
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    Member member = memberRepo.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    if (likeRepo.existsByPost_IdAndMember_Id(postId, memberId)) {
      likeRepo.deleteByPost_IdAndMember_Id(postId, memberId);
    } else {
      PostLike like = PostLike.builder()
          .post(post)
          .member(member)
          .build();
      likeRepo.save(like);
    }

    long currentLikes = likeRepo.countByPost_Id(postId);
    post.setLikeCount((int) currentLikes);
    postRepo.save(post);
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
    postRepo.findById(postId)
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    memberRepo.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    return likeRepo.existsByPost_IdAndMember_Id(postId, memberId);
  }
}
