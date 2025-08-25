package com.example.demo.domain.post.dao;

import com.example.demo.domain.post.model.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.example.demo.domain.post.dao
 * FileName    : PostRepositoryImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private static final QPost POST = QPost.post;

    private final JPAQueryFactory jpaQueryFactory;

}
