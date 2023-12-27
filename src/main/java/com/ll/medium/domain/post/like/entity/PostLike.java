package com.ll.medium.domain.post.like.entity;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "member_id"}))
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class PostLike extends BaseEntity {
    @ManyToOne
    private Post post;

    @ManyToOne
    private Member member;
}
