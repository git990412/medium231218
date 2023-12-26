package com.ll.medium.domain.post.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.post.like.entity.Like;
import com.ll.medium.domain.post.like.repository.LikeRepository;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createLike(Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        likeRepository.save(
                Like.builder()
                        .member(member)
                        .post(post)
                        .build());
    }

    @Transactional
    public void cancelLike(Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        likeRepository.delete(
                likeRepository.findByMemberAndPost(member, post).orElseThrow());
    }
}
