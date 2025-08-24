package com.example.demo.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * PackageName : com.example.demo.common.config.properties
 * FileName    : DataDBSourceProperties
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@ConfigurationProperties(prefix = "spring.datasource-data")
@Getter
@Setter
public class DataDBSourceProperties {

    private String driverClassName;
    private String jdbcUrl;
    private String username;
    private String password;
    private Hikari hikari;

    @Getter
    @Setter
    static class Hikari {
        private String poolName;
        private int    maximumPoolSize;
        private int    minimumIdle;
        private int    idleTimeout;
    }

}
