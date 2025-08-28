package com.example.demo.infra.elasticsearch.post.config;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import com.example.demo.common.jobmeta.event.listener.JobMetaDataUpdateListener;
import com.example.demo.domain.post.model.PostDto;
import com.example.demo.infra.elasticsearch.common.job.step.GenericElasticsearchBulkItemWriter;
import com.example.demo.infra.elasticsearch.post.model.PostDocument;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * PackageName : com.example.demo.infra.elasticsearch.post.config
 * FileName    : PostDbToEsConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Configuration
@Slf4j
public class PostDbToEsConfig {

    private static final int CHUNK_SIZE = 1000;

    private final JobBuilderFactory         jobBuilderFactory;
    private final StepBuilderFactory        stepBuilderFactory;
    //private final EntityManagerFactory      entityManagerFactory;
    //private final PostSearchRepository      postSearchRepository;
    private final DataSource                dataSource;
    private final PagingQueryProvider       pagingQueryProvider;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final JobMetaDataUpdateListener jobMetaDataUpdateListener;

    public PostDbToEsConfig(
            final JobBuilderFactory jobBuilderFactory,
            final StepBuilderFactory stepBuilderFactory,
            //@Qualifier("dataEntityManagerFactory") final EntityManagerFactory entityManagerFactory,
            //final PostSearchRepository postSearchRepository,
            @Qualifier("dataDBSource") final DataSource dataSource,
            @Qualifier("postPagingQueryProvider") final PagingQueryProvider pagingQueryProvider,
            final ElasticsearchRestTemplate elasticsearchRestTemplate,
            final JobMetaDataUpdateListener jobMetaDataUpdateListener
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        //this.entityManagerFactory = entityManagerFactory;
        //this.postSearchRepository = postSearchRepository;
        this.dataSource = dataSource;
        this.pagingQueryProvider = pagingQueryProvider;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.jobMetaDataUpdateListener = jobMetaDataUpdateListener;
    }

    @Bean
    public Job postDbToEsMigrationJob() {
        return jobBuilderFactory.get("postDbToEsMigrationJob")
                                .incrementer(new RunIdIncrementer())
                                .listener(jobMetaDataUpdateListener)
                                .start(postDbToEsStep())
                                .build();
    }

    @Bean
    public Step postDbToEsStep() {
        return stepBuilderFactory.get("postDbToEsStep")
                                 .<PostDto, PostDocument>chunk(CHUNK_SIZE)
                                 .reader(postJdbcPagingItemReader(null))
                                 .processor(postDtoToPostDocumentItemProcessor())
                                 .writer(postDocumentItemWriter())
                                 .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<PostDto> postJdbcPagingItemReader(
            @Value("#{jobParameters['lastIndexedAt']}") final String lastIndexedAtStr
    ) {
        return new JdbcPagingItemReaderBuilder<PostDto>()
                .name("postJdbcPagingItemReader")
                .dataSource(dataSource)
                .queryProvider(pagingQueryProvider)
                .parameterValues(
                        Collections.singletonMap(
                                "lastIndexedAt", LocalDateTime.parse(lastIndexedAtStr, ISO_LOCAL_DATE_TIME)
                        )
                )
                .pageSize(CHUNK_SIZE)
                //.rowMapper(new BeanPropertyRowMapper<>(PostDto.class))
                .rowMapper((rs, rowNum) -> {
                    try {
                        return PostDto.builder()
                                      .id(rs.getLong("post_id"))
                                      .writerId(rs.getObject("member_id", UUID.class))
                                      .writer(rs.getString("nickname"))
                                      .title(rs.getString("title"))
                                      .content(rs.getString("content"))
                                      .viewCount(rs.getLong("view_count"))
                                      .likeCount(rs.getInt("like_count"))
                                      .isDeleted(rs.getBoolean("is_deleted"))
                                      .commentCount(rs.getInt("comment_count"))
                                      .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                                      .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                                      .build();
                    } catch (SQLException e) {
                        long errorId = -1;
                        try {
                            errorId = rs.getLong("post_id");
                        } catch (SQLException ignored) {}
                        log.error("FAILED TO MAP ROW to PostDto. ID: {}", errorId, e);
                        throw new DataIntegrityViolationException(
                                "Failed to map row #" + rowNum + " with ID: " + errorId, e
                        );
                    }
                })
                .build();
    }

//    @Bean
//    public JpaPagingItemReader<Post> postJpaPagingItemReader() {
//        return new JpaPagingItemReaderBuilder<Post>()
//                .name("postJpaPagingItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("SELECT p FROM posts p ORDER BY p.id ASC")
//                .pageSize(CHUNK_SIZE)
//                .saveState(true)
//                .transacted(true)
//                .build();
//    }

    @Bean
    public ItemProcessor<PostDto, PostDocument> postDtoToPostDocumentItemProcessor() {
        return PostDocument::from;
    }

//    @Bean
//    public ItemProcessor<Post, PostDocument> postToPostDocumentItemProcessor() {
//        return PostDocument::from;
//    }

    @Bean
    public ItemWriter<PostDocument> postDocumentItemWriter() {
        return new GenericElasticsearchBulkItemWriter<>(elasticsearchRestTemplate, "posts");
    }

//    @Bean
//    public ItemWriter<PostDocument> postDocumentItemWriter() {
//        return postSearchRepository::saveAll;
//    }

}
