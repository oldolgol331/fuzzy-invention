package com.example.demo.infra.elasticsearch.common.job.step;

import com.example.demo.infra.elasticsearch.common.document.ElasticsearchDocument;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

/**
 * PackageName : com.example.demo.infra.elasticsearch.common.job.step
 * FileName    : GenericElasticsearchBulkItemWriter
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@RequiredArgsConstructor
@Slf4j
public class GenericElasticsearchBulkItemWriter<T extends ElasticsearchDocument> implements ItemWriter<T> {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final String                    indexName;

    @Override
    public void write(List<? extends T> items) throws Exception {
        if (items.isEmpty()) return;

        List<IndexQuery> queries = items.stream()
                                        .map(
                                                item -> new IndexQueryBuilder()
                                                        .withId(item.getId())
                                                        .withObject(item)
                                                        .build()
                                        )
                                        .collect(Collectors.toList());

        elasticsearchRestTemplate.bulkIndex(queries, IndexCoordinates.of(indexName));

        log.info("Bulk indexed {} documents into index '{}'", items.size(), indexName);
    }

}
