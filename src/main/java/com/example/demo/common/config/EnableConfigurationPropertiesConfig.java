package com.example.demo.common.config;

import com.example.demo.common.config.properties.DataDBSourceProperties;
import com.example.demo.common.config.properties.MetaDBSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PackageName : com.example.demo.common.config
 * FileName    : EnableConfigurationPropertiesConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Configuration
@EnableConfigurationProperties({DataDBSourceProperties.class, MetaDBSourceProperties.class})
public class EnableConfigurationPropertiesConfig {
}
