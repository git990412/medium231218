package com.ll.medium.domain.post.post.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.global.jpa.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Post extends BaseEntity {
    private String title;
    private String body;
    private boolean isPublished;
    private Long hit;
    private boolean isPaid;

    @ManyToOne(fetch = LAZY)
    private Member member;
}