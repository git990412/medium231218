package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.post.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Page<Post> findByIsPublishedTrueOrderByIdDesc(Pageable pageable);

    Page<Post> findByMemberIdOrderByIdDesc(Pageable pageable, long userId);
}