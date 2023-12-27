package com.ll.medium.domain.post.like.repository;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.like.entity.PostLike;
import com.ll.medium.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMemberAndPost(Member member, Post post);

}
