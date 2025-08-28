package com.example.demo.domain.post.service;

import static com.example.demo.common.response.ErrorCode.JOB_METADATA_NOT_FOUND;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import com.example.demo.common.error.CustomException;
import com.example.demo.common.jobmeta.dao.JobMetaDataRepository;
import com.example.demo.common.jobmeta.model.JobMetaData;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostBatchJobServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Service
@Slf4j
public class PostBatchJobServiceImpl implements PostBatchJobService {

    private final JobLauncher           jobLauncher;
    private final Job                   postSyncJob;
    private final JobMetaDataRepository jobMetaDataRepository;

    public PostBatchJobServiceImpl(
            final JobLauncher jobLauncher,
            @Qualifier("postDbToEsMigrationJob") final Job postSyncJob,
            final JobMetaDataRepository jobMetaDataRepository
    ) {
        this.jobLauncher = jobLauncher;
        this.postSyncJob = postSyncJob;
        this.jobMetaDataRepository = jobMetaDataRepository;
    }

    @Async
    @Override
    public void runPostSyncJob() {
        JobMetaData jobMetaData = jobMetaDataRepository.findByJobName(postSyncJob.getName())
                                                       .orElseThrow(() -> new CustomException(JOB_METADATA_NOT_FOUND));
        LocalDateTime lastIndexedAt = jobMetaData.getLastSuccessfulRunTime();
        try {
            log.info("DB to ES sync job started");
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("lastIndexedAt", lastIndexedAt.format(ISO_LOCAL_DATE_TIME))
                    .addDate("scheduledAt", new Date(System.currentTimeMillis()))
                    .toJobParameters();
            jobLauncher.run(postSyncJob, jobParameters);
        } catch (Exception e) {
            log.error("DB to ES sync job failed", e);
        }
    }

}
