package com.example.demo.domain.post.constant;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.post.constant
 * FileName    : PostConst
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public class PostConst {

    public static final String POST_VIEW_LOG_KEY_PREFIX   = "post:view:log:%s:%s";
    public static final String POST_VIEW_COUNT_KEY_PREFIX = "post:view:count:%s";
    public static final String POST_VIEW_COUNT_PATTERN    = "post:view:count:*";
    public static final String POST_VIEW_KEY_PREFIX       = "post:view:%s";

}
