package com.example.demo.common.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Repository;

/**
 * PackageName : com.example.demo.common.config.annotation
 * FileName    : DataDBJpaRepositoryMarker
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repository
public @interface DataDBJpaRepositoryMarker {
}
