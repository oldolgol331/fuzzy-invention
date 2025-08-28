package com.example.demo.domain.post.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.post.model
 * FileName    : PostDto
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private Long          id;
    private UUID          writerId;
    private String        writer;
    private String        title;
    private String        content;
    private Long          viewCount;
    private Integer       likeCount;
    private Boolean       isDeleted;
    private Integer       commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
