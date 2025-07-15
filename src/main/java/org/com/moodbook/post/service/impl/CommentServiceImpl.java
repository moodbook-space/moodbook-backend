package org.com.moodbook.post.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.com.moodbook.common.exception.BaseException;
import org.com.moodbook.common.exception.ErrorCode;
import org.com.moodbook.member.repository.MemberRepository;
import org.com.moodbook.post.dto.CommentResponse;
import org.com.moodbook.post.dto.CreateCommentRequest;
import org.com.moodbook.post.entity.Comment;
import org.com.moodbook.post.repository.BasePostRepository;
import org.com.moodbook.post.repository.CommentRepository;
import org.com.moodbook.post.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
  private final CommentRepository commentRepository;
  private final BasePostRepository postRepository;
  private final MemberRepository memberRepository;

  @Override
  public Long addComment(Long memberId, CreateCommentRequest req) {
    var post = postRepository.findById(req.getPostId())
        .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    var member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

    Comment parent = null;
    if (req.getParentCommentId() != null) {
      parent = commentRepository.findById(req.getParentCommentId())
          .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
    }

    Comment c = Comment.builder()
        .post(post)
        .author(member)
        .content(req.getContent())
        .parentComment(parent)
        .build();
    return commentRepository.save(c).getId();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommentResponse> getComments(Long memberId, Long postId) {
    // 최상위 댓글부터 조회
    var roots = commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(postId);
    return roots.stream().map(root -> toDto(root, memberId)).toList();
  }

  private CommentResponse toDto(Comment c, Long memberId) {
    // 대댓글 조회
    var replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(c.getId())
        .stream()
        .map(r -> toDto(r, memberId))
        .toList();

    return CommentResponse.builder()
        .id(c.getId())
        .authorId(c.getAuthor().getId())
        .authorName(c.getAuthor().getName())
        .content(c.getContent())
        .createdAt(c.getCreatedAt())
        .isMine(c.getAuthor().getId().equals(memberId))
        .replies(replies)
        .build();
  }

  @Override
  public void deleteComment(Long memberId, Long commentId) {
    var c = commentRepository.findById(commentId)
        .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_NOT_FOUND));
    if (!c.getAuthor().getId().equals(memberId)) {
      throw new BaseException(ErrorCode.ACCESS_DENIED);
    }
    commentRepository.delete(c);
  }
}