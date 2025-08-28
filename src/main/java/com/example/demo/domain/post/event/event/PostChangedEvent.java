package com.example.demo.domain.post.event.event;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.common.event.type.ChangeType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : com.example.demo.domain.post.event.event
 * FileName    : PostChangedEvent
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class PostChangedEvent {

    private final Long       postId;
    private final ChangeType changeType;

    public static PostChangedEvent of(final Long postId, final ChangeType changeType) {
        return PostChangedEvent.builder().postId(postId).changeType(changeType).build();
    }

}
