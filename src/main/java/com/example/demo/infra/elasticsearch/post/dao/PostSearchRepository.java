package com.example.demo.infra.elasticsearch.post.dao;

import com.example.demo.infra.elasticsearch.post.model.PostDocument;
import com.example.demo.infra.redis.config.annotation.ElasticsearchRepositoryMarker;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.dao
 * FileName    : PostSearchRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@ElasticsearchRepositoryMarker
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, Long>, PostSearchRepositoryCustom {
}
