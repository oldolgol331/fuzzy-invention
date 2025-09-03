package com.example.demo.infra.redis.config;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * PackageName : com.example.demo.infra.redis.config
 * FileName    : ListKeyGenerator
 * Author      : oldolgol331
 * Date        : 25. 9. 4.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 4.     oldolgol331          Initial creation
 */
@Profile("!test")
@Component("listKeyGenerator")
public class ListKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringJoiner key = new StringJoiner(":");

        key.add(target.getClass().getSimpleName());
        key.add(method.getName());

        for (Object param : params) {
            if (param == null) key.add("null");
            else if (param instanceof String) key.add(param.toString());
            else if (param instanceof Pageable) {
                Pageable pageable = (Pageable) param;
                key.add(String.valueOf(pageable.getPageNumber()));
                key.add(String.valueOf(pageable.getPageSize()));
                key.add(pageable.getSort().toString().replace(": ", "_"));
            }
        }

        return key.toString();
    }

}
