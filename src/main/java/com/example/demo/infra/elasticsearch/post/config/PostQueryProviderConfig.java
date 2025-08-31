package com.example.demo.infra.elasticsearch.post.config;

import javax.sql.DataSource;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.config
 * FileName    : PostQueryProviderConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Configuration
public class PostQueryProviderConfig {

    private final DataSource dataSource;

    public PostQueryProviderConfig(@Qualifier("dataDBSource") final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PagingQueryProvider postPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause(
                "SELECT" +
                " P.post_id AS post_id," +
                " P.member_id AS member_id," +
                " M.nickname AS nickname," +
                " P.title AS title," +
                " P.content AS content," +
                " P.view_count AS view_count," +
                " P.like_count AS like_count," +
                " P.is_deleted AS is_deleted," +
                //" (SELECT COUNT(C.comment_id) FROM comments AS C WHERE C.post_id = P.post_id) AS comment_count," +
                " P.comment_count AS comment_count," +
                " P.created_at AS created_at," +
                " P.updated_at AS updated_at"
        );
        provider.setFromClause(
                "FROM posts AS P" +
                " JOIN members AS M ON P.member_id = M.member_id"
        );
        provider.setWhereClause(
                "WHERE P.updated_at > :lastIndexedAt"
        );
        provider.setSortKey("post_id");
        return provider.getObject();
    }

}
