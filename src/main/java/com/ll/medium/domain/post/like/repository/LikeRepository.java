package com.ll.medium.domain.post.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.like.entity.Like;
import com.ll.medium.domain.post.post.entity.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndPost(Member member, Post post);

}
