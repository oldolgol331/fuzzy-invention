package com.example.demo.common.util;

import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

/**
 * PackageName : com.example.demo.common.util
 * FileName    : PageableUtils
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public final class PageableUtils {

    private static final Pattern SORT_PATTERN = Pattern.compile("([^,]+?)(,(?i)(DESC|ASC))?");

    /**
     * "property1,direction1,property2,direction2,..." 형식의 정렬 문자열을 정규표현식을 사용하여
     * Spring Data의 Sort 객체로 변환합니다.
     *
     * @param sort - 정렬 기준 문자열
     * @return 변환된 Sort 객체
     */
    public static Sort parseSort(final String sort) {
        if (!StringUtils.hasText(sort)) return Sort.unsorted();

        final List<Sort.Order> orders  = new ArrayList<>();
        final Matcher          matcher = SORT_PATTERN.matcher(sort);

        while (matcher.find()) {
            final String property     = matcher.group(1).trim();
            final String directionStr = matcher.group(3);

            final Sort.Direction direction = (directionStr != null && directionStr.equalsIgnoreCase("DESC"))
                                             ? Sort.Direction.DESC : Sort.Direction.ASC;

            orders.add(new Sort.Order(direction, property));
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }

}
