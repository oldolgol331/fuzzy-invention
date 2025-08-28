package com.example.demo.domain.post.scheduler;

import com.example.demo.domain.post.service.PostBatchJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PackageName : com.example.demo.domain.post.scheduler
 * FileName    : PostSyncScheduler
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
public class PostSyncScheduler {

    private final PostBatchJobService postBatchJobService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void runSyncJob() {
        postBatchJobService.runPostSyncJob();
    }

}
