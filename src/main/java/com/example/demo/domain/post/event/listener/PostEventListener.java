package com.example.demo.domain.post.event.listener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.example.demo.common.event.type.ChangeType;
import com.example.demo.domain.post.dao.PostRepository;
import com.example.demo.domain.post.event.event.PostChangedEvent;
import com.example.demo.infra.elasticsearch.post.dao.PostSearchRepository;
import com.example.demo.infra.elasticsearch.post.model.PostDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * PackageName : com.example.demo.domain.post.event.listener
 * FileName    : PostEventListener
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final PostRepository       postRepository;
    private final PostSearchRepository postSearchRepository;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handlePostChangeEvent(final PostChangedEvent event) {
        final Long       postId     = event.getPostId();
        final ChangeType changeType = event.getChangeType();

        switch (changeType) {
            case CREATED:
            case UPDATED:
                postRepository.findByIdAndIsDeletedFalse(postId)
                              .ifPresent(post -> postSearchRepository.save(PostDocument.from(post)));
                break;
            case DELETED:
                postSearchRepository.deleteById(postId);
                break;
        }
    }

}
