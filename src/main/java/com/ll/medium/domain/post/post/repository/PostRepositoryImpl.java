package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.post.post.entity.Post;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import static com.ll.medium.domain.post.post.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Post> findByKw(String kwType, String kw, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        switch (kwType) {
            case "title" -> builder.and(post.title.containsIgnoreCase(kw));
            case "body" -> builder.and(post.body.containsIgnoreCase(kw));
            case "nickname" -> builder.and(post.member.username.containsIgnoreCase(kw));
            case "title,body" -> builder.and(post.title.containsIgnoreCase(kw).or(post.body.containsIgnoreCase(kw)));
            default -> builder.and(
                    post.title.containsIgnoreCase(kw)
                            .or(post.body.containsIgnoreCase(kw))
                            .or(post.member.username.containsIgnoreCase(kw))
            );
        }

        JPAQuery<Post> articlesQuery = jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .where(builder);

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(post.getType(), post.getMetadata());
            articlesQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        articlesQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        JPAQuery<Long> totalQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(builder);

        return PageableExecutionUtils.getPage(articlesQuery.fetch(), pageable, totalQuery::fetchOne);
    }
}
