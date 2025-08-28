package com.example.demo.common.jobmeta.dao;

import com.example.demo.common.config.annotation.MetaDBJpaRepositoryMarker;
import com.example.demo.common.jobmeta.model.JobMetaData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.common.jobmeta.dao
 * FileName    : JobMetaDataRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@MetaDBJpaRepositoryMarker
public interface JobMetaDataRepository extends JpaRepository<JobMetaData, String> {

    Optional<JobMetaData> findByJobName(String jobName);

}
