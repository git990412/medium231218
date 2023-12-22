package com.ll.medium.domain.post.post.dto;

import java.time.LocalDateTime;

import com.ll.medium.domain.member.member.dto.MemberDto;
import com.ll.medium.domain.post.post.entity.Post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {
  private Long id;
  private String title;
  private String body;
  private LocalDateTime createDate;
  private LocalDateTime modifyDate;
  private boolean isPublished;
  private boolean isPaid;
  private Long hit;
  private MemberDto member;

  public PostDto(Post post) {
    this.id = post.getId();
    this.title = post.getTitle();
    this.body = post.getBody();
    this.hit = post.getHit();
    this.isPublished = post.isPublished();
    this.createDate = post.getCreateDate();
    this.modifyDate = post.getModifyDate();
    this.member = new MemberDto(post.getMember());
    this.isPaid = post.isPaid();
  }
}