package com.example.demo.common.response.util;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.common.response.annotation.CustomPageResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * PackageName : com.example.demo.common.response.util
 * FileName    : PageResponseConverter
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public class PageResponseConverter {

    public static Map<String, Object> convertPageToCustomMap(final Page<?> page, final CustomPageResponse annotation) {
        Map<String, Object> map = new HashMap<>();
        if (annotation.content()) map.put("content", page.getContent());
        if (annotation.totalElements()) map.put("totalElements", page.getTotalElements());
        if (annotation.totalPages()) map.put("totalPages", page.getTotalPages());
        if (annotation.size()) map.put("size", page.getSize());
        if (annotation.number()) map.put("number", page.getNumber());
        if (annotation.numberOfElements()) map.put("numberOfElements", page.getNumberOfElements());
        if (annotation.sort()) map.put("sort", page.getSort());
        if (annotation.empty()) map.put("empty", page.isEmpty());
        if (annotation.hasContent()) map.put("hasContent", page.hasContent());
        if (annotation.first()) map.put("first", page.isFirst());
        if (annotation.last()) map.put("last", page.isLast());
        if (annotation.hasPrevious()) map.put("hasPrevious", page.hasPrevious());
        if (annotation.hasNext()) map.put("hasNext", page.hasNext());
        return map;
    }

}
