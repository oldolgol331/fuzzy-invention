package com.example.demo.common.config.dialect;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

/**
 * PackageName : com.example.demo.common.config.dialect
 * FileName    : CustomMySQLDialect
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
public class CustomMySQLDialect extends MySQL8Dialect {

    public CustomMySQLDialect() {
        super();
        registerFunction(
                "fulltext_boolean_search_param_2",
                new SQLFunctionTemplate(
                        StandardBasicTypes.DOUBLE,
                        "MATCH (?1, ?2) AGAINST (?3 IN BOOLEAN MODE)"
                )
        );
    }

}
