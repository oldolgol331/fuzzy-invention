package com.example.demo.common.jobmeta.event.listener;

import static com.example.demo.common.response.ErrorCode.JOB_METADATA_NOT_FOUND;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import com.example.demo.common.error.CustomException;
import com.example.demo.common.jobmeta.dao.JobMetaDataRepository;
import com.example.demo.common.jobmeta.model.JobMetaData;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * PackageName : com.example.demo.common.jobmeta.event.listener
 * FileName    : JobMetaDataUpdateListener
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
@Slf4j
public class JobMetaDataUpdateListener implements JobExecutionListener {

    private final JobMetaDataRepository jobMetaDataRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {}

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == COMPLETED) {
            String jobName = jobExecution.getJobInstance().getJobName();
            JobMetaData jobMetaData = jobMetaDataRepository.findByJobName(jobName)
                                                           .orElseThrow(
                                                                   () -> new CustomException(JOB_METADATA_NOT_FOUND)
                                                           );
            jobMetaData.setLastSuccessfulRunTime(LocalDateTime.now());
            jobMetaDataRepository.save(jobMetaData);
            log.info("Successfully updated last successful run time for job: {}", jobName);
        }
    }

}
