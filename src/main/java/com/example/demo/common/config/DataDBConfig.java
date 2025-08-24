package com.example.demo.common.config;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.common.config.properties.DataDBSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * PackageName : com.example.demo.common.config
 * FileName    : DataDBConfig
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
        basePackages = "com.example.demo.domain",
        includeFilters = @ComponentScan.Filter(
                type = ANNOTATION, classes = DataDBJpaRepositoryMarker.class
        ),
        entityManagerFactoryRef = "dataEntityManagerFactory",
        transactionManagerRef = "dataTransactionManager"
)
@RequiredArgsConstructor
public class DataDBConfig {

    private final DataDBSourceProperties properties;

    @Bean(name = "dataDBSource")
    @Primary
    public DataSource dataDBSource() {
        return DataSourceBuilder.create()
                                .driverClassName(properties.getDriverClassName())
                                .url(properties.getJdbcUrl())
                                .username(properties.getUsername())
                                .password(properties.getPassword())
                                .type(HikariDataSource.class)
                                .build();
    }

    @Bean(name = "dataJdbcTemplate")
    @Primary
    public JdbcTemplate dataJdbcTemplate(@Qualifier("dataDBSource") final DataSource dataDBSource) {
        return new JdbcTemplate(dataDBSource);
    }

    @Bean(name = "dataEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean dataEntityManagerFactory(
            @Qualifier("dataDBSource") final DataSource dataDBSource
    ) {
        LocalContainerEntityManagerFactoryBean emFactory = new LocalContainerEntityManagerFactoryBean();
        emFactory.setDataSource(dataDBSource);
        emFactory.setPackagesToScan("com.example.demo.domain");
        emFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        //properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        emFactory.setJpaPropertyMap(properties);

        return emFactory;
    }

    @Bean(name = "dataTransactionManager")
    @Primary
    public AbstractPlatformTransactionManager dataTransactionManager(
            @Qualifier("dataEntityManagerFactory") final EntityManagerFactory dataEntityManagerFactory
    ) {
        return new JpaTransactionManager(dataEntityManagerFactory);
    }

}
