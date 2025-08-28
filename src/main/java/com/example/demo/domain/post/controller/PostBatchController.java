package com.example.demo.domain.post.controller;

import static com.example.demo.common.response.SuccessCode.POST_BATCH_SYNC_SUCCESS;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.domain.post.service.PostBatchJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageName : com.example.demo.domain.post.controller
 * FileName    : PostBatchController
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@RestController
@RequestMapping("/api/v1/posts/batch")
@RequiredArgsConstructor
@Tag(name = "게시글 배치 API", description = "게시글 관련 배치 로직 호출 API를 제공합니다.")
public class PostBatchController {

    private final PostBatchJobService postBatchJobService;

    @PostMapping
    @Operation(summary = "게시글 배치(DB -> ES) 작업 호출", description = "게시글 배치 작업(DB -> ES)을 호출합니다.")
    public ResponseEntity<ApiResponse<Void>> runPostSyncJob() {
        postBatchJobService.runPostSyncJob();
        return ResponseEntity.ok(ApiResponse.success(POST_BATCH_SYNC_SUCCESS));
    }

}
