package com.example.demo.common.jobmeta.model;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.demo.common.model.BaseAuditingEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PackageName : com.example.demo.common.jobmeta.model
 * FileName    : JobMetaData
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Entity
@Table(name = "JOB_METADATA")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class JobMetaData extends BaseAuditingEntity {

    @Id
    @Column(name = "JOB_NAME", nullable = false, updatable = false)
    @NotBlank
    private String jobName;

    @Column(name = "LAST_SUCCESSFUL_RUN_TIME", nullable = false)
    @Setter
    @NotNull
    private LocalDateTime lastSuccessfulRunTime;

    @Version
    @Column(name = "version", nullable = false)
    @NotNull
    private Long version;

}
