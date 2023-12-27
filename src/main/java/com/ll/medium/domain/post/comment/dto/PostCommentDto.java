package com.ll.medium.domain.post.comment.dto;

import com.ll.medium.domain.post.comment.entity.PostComment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostCommentDto {
    private long id;
    private String username;
    private String body;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public PostCommentDto(PostComment postComment) {
        this.id = postComment.getId();
        this.username = postComment.getMember().getUsername();
        this.body = postComment.getBody();
        this.createDate = postComment.getCreateDate();
        this.modifyDate = postComment.getModifyDate();
    }
}
