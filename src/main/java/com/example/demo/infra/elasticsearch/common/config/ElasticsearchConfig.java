package com.example.demo.infra.elasticsearch.common.config;

import static org.apache.http.auth.AuthScope.ANY;
import static org.springframework.context.annotation.FilterType.ANNOTATION;

import com.example.demo.infra.redis.config.annotation.ElasticsearchRepositoryMarker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * PackageName : com.example.demo.infra.elasticsearch.common.config
 * FileName    : ElasticsearchConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Configuration
@EnableElasticsearchRepositories(
        basePackages = "com.example.demo.infra.elasticsearch",
        includeFilters = @ComponentScan.Filter(type = ANNOTATION, classes = ElasticsearchRepositoryMarker.class)
)
@RequiredArgsConstructor
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    private final ElasticsearchProperties properties;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        final List<String> uris  = properties.getUris();
        final HttpHost[]   hosts = new HttpHost[uris.size()];
        for (int i = 0; i < uris.size(); i++) hosts[i] = HttpHost.create(uris.get(i));
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                ANY, new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword())
        );
        return new RestHighLevelClientBuilder(
                RestClient.builder(hosts)
                          .setHttpClientConfigCallback(
                                  httpClientBuilder ->
                                          httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                          )
                          .build()
        )
                .setApiCompatibilityMode(true)
                .build();
    }

    @Bean
    public ElasticsearchRestTemplate elasticsearchRestTemplate(final RestHighLevelClient restHighLevelClient) {
        return new ElasticsearchRestTemplate(restHighLevelClient);
    }

}
