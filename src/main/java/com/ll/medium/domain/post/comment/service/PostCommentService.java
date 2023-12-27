package com.ll.medium.domain.post.comment.service;

import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.post.comment.entity.PostComment;
import com.ll.medium.domain.post.comment.repository.PostCommentRepository;
import com.ll.medium.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void addComment(String comment, Long postId, Long memberId) {
        postRepository.findById(postId).ifPresent(post -> {
            memberRepository.findById(memberId).ifPresent(member -> {
                postCommentRepository.save(PostComment.builder()
                        .body(comment)
                        .post(post)
                        .member(member)
                        .build());
            });
        });
    }

    @Transactional
    public void updateComment(Long id, String body) {
        PostComment comment = postCommentRepository.findById(id).get();
        comment.setBody(body);
        postCommentRepository.save(comment);
    }

    public PostComment getComment(Long id) {
        return postCommentRepository.findById(id).get();
    }

    @Transactional
    public void deleteComment(Long id) {
        postCommentRepository.deleteById(id);
    }
}
