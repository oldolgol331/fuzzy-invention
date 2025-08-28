package com.example.demo.common.config;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

import com.example.demo.common.config.annotation.MetaDBJpaRepositoryMarker;
import com.example.demo.common.config.properties.MetaDBSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * PackageName : com.example.demo.common.config
 * FileName    : MetaDBConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.demo.common",
        includeFilters = @ComponentScan.Filter(
                type = ANNOTATION, classes = MetaDBJpaRepositoryMarker.class
        ),
        entityManagerFactoryRef = "metaEntityManagerFactory",
        transactionManagerRef = "metaTransactionManager"
)
@RequiredArgsConstructor
public class MetaDBConfig {

    private final MetaDBSourceProperties properties;

    @Bean(name = "metaDBSource")
    @BatchDataSource
    public DataSource metaDBSource() {
        return DataSourceBuilder.create()
                                .driverClassName(properties.getDriverClassName())
                                .url(properties.getJdbcUrl())
                                .username(properties.getUsername())
                                .password(properties.getPassword())
                                .type(HikariDataSource.class)
                                .build();
    }

    @Bean(name = "metaJdbcTemplate")
    public JdbcTemplate metaJdbcTemplate(@Qualifier("metaDBSource") final DataSource metaDBSource) {
        return new JdbcTemplate(metaDBSource);
    }

    @Bean(name = "metaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean metaEntityManagerFactory(
            @Qualifier("metaDBSource") final DataSource metaDBSource
    ) {
        LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setDataSource(metaDBSource);
        emFactory.setPackagesToScan("com.example.demo.common");
        emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        //properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        emFactory.setJpaPropertyMap(properties);

        return emFactory;
    }

    @Bean(name = "metaTransactionManager")
    public AbstractPlatformTransactionManager metaTransactionManager(
            @Qualifier("metaEntityManagerFactory") final EntityManagerFactory metaEntityManagerFactory
    ) {
        return new JpaTransactionManager(metaEntityManagerFactory);
    }

}
