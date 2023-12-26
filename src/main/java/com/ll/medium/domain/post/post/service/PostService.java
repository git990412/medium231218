package com.ll.medium.domain.post.post.service;

import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.form.WriteForm;
import com.ll.medium.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Page<PostDto> getLatest30() {
        Pageable pageable = PageRequest.of(0, 30);
        return postRepository.findByIsPublishedTrueOrderByIdDesc(pageable).map(PostDto::new);
    }

    public Page<PostDto> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findByIsPublishedTrueOrderByIdDesc(pageable).map(PostDto::new);
    }

    public Page<PostDto> getMyList(int page, long userId) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findByMemberIdOrderByIdDesc(pageable, userId).map(PostDto::new);
    }

    public Post getOne(long id) {
        return postRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void writePost(WriteForm form, Long id) {
        Post post = Post.builder()
                .title(form.getTitle())
                .body(form.getBody())
                .isPublished(form.isPublished())
                .member(memberRepository.findById(id).orElseThrow())
                .build();

        postRepository.save(post);
    }

    @Transactional
    public void updatePost(WriteForm form, Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(form.getTitle());
        post.setBody(form.getBody());
        post.setPublished(form.isPublished());
    }

    public Long getPostMemberId(long id) {
        return postRepository.findById(id).orElseThrow().getMember().getId();
    }

    @Transactional
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public void increaseHit(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        if (post.getHit() == null) {
            post.setHit(1L);
        } else {
            post.setHit(post.getHit() + 1);
        }
        postRepository.save(post);
    }
}