package com.example.demo.common.config;

import com.example.demo.common.config.formatter.P6SpySqlFormatter;
import com.p6spy.engine.spy.P6SpyOptions;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * PackageName : com.example.demo.common.config
 * FileName    : P6SpyConfig
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Configuration
public class P6SpyConfig {

    @PostConstruct
    private void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6SpySqlFormatter.class.getName());
    }

}
