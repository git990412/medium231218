package com.ll.medium.domain.post.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1879897337L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final com.ll.medium.global.jpa.entity.QBaseEntity _super = new com.ll.medium.global.jpa.entity.QBaseEntity(this);

    public final StringPath body = createString("body");

    public final ListPath<com.ll.medium.domain.post.comment.entity.PostComment, com.ll.medium.domain.post.comment.entity.QPostComment> comments = this.<com.ll.medium.domain.post.comment.entity.PostComment, com.ll.medium.domain.post.comment.entity.QPostComment>createList("comments", com.ll.medium.domain.post.comment.entity.PostComment.class, com.ll.medium.domain.post.comment.entity.QPostComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> hit = createNumber("hit", Long.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isPaid = createBoolean("isPaid");

    public final BooleanPath isPublished = createBoolean("isPublished");

    public final ListPath<com.ll.medium.domain.post.like.entity.PostLike, com.ll.medium.domain.post.like.entity.QPostLike> likes = this.<com.ll.medium.domain.post.like.entity.PostLike, com.ll.medium.domain.post.like.entity.QPostLike>createList("likes", com.ll.medium.domain.post.like.entity.PostLike.class, com.ll.medium.domain.post.like.entity.QPostLike.class, PathInits.DIRECT2);

    public final com.ll.medium.domain.member.member.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifyDate = _super.modifyDate;

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.ll.medium.domain.member.member.entity.QMember(forProperty("member")) : null;
    }

}

