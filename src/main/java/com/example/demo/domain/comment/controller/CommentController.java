package com.example.demo.domain.comment.controller;

import static com.example.demo.common.response.SuccessCode.COMMENT_DELETE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.COMMENT_LIKE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.COMMENT_LIST_READ_SUCCESS;
import static com.example.demo.common.response.SuccessCode.COMMENT_UPDATE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.COMMENT_WRITE_SUCCESS;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.common.util.PageableUtils;
import com.example.demo.domain.comment.dto.CommentRequest.CommentCreateRequest;
import com.example.demo.domain.comment.dto.CommentRequest.CommentUpdateRequest;
import com.example.demo.domain.comment.dto.CommentResponse.CommentListResponse;
import com.example.demo.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PackageName : com.example.demo.domain.comment.controller
 * FileName    : CommentController
 * Author      : oldolgol331
 * Date        : 25. 8. 27.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 27.    oldolgol331          Initial creation
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "댓글 API", description = "댓글 작성, 수정, 삭제 API를 제공합니다.")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    @Operation(summary = "댓글 작성", description = "새 댓글을 작성합니다.")
    public ResponseEntity<ApiResponse<Void>> createComment(
            @PathVariable("postId") final Long postId,
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final CommentCreateRequest request
    ) {
        commentService.createComment(postId, userDetails.getId(), request);
        final SuccessCode successCode = COMMENT_WRITE_SUCCESS;
        return ResponseEntity.status(successCode.getStatus())
                             .body(ApiResponse.success(successCode));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "댓글 목록 조회", description = "특정 게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<CommentListResponse>>> getPosts(
            @PathVariable("postId") final Long postId,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(1) final int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) final int size,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt,DESC") final String sort,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        Sort                      orders       = PageableUtils.parseSort(sort);
        PageRequest               pageRequest  = PageRequest.of(page - 1, size, orders);
        Page<CommentListResponse> responseData = commentService.getComments(postId, userDetails.getId(), pageRequest);
        return ResponseEntity.ok(ApiResponse.success(COMMENT_LIST_READ_SUCCESS, responseData));
    }

    @PutMapping("/{postId}/{id}")
    @Operation(summary = "댓글 수정", description = "특정 댓글의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable("postId") final Long postId,
            @PathVariable("id") final Long commentId,
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final CommentUpdateRequest request
    ) {
        commentService.updateComment(commentId, postId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(COMMENT_UPDATE_SUCCESS));
    }

    @DeleteMapping("/{postId}/{id}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("postId") final Long postId,
            @PathVariable("id") final Long commentId,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        commentService.deleteComment(commentId, postId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(COMMENT_DELETE_SUCCESS));
    }

    @PatchMapping("/{postId}/{id}")
    @Operation(summary = "댓글 좋아요 추가/취소", description = "특정 댓글에 좋아요를 추가 또는 좋아요를 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> likeComment(
            @PathVariable("postId") final Long postId,
            @PathVariable("id") final Long commentId,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        commentService.likeComment(commentId, postId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(COMMENT_LIKE_SUCCESS));
    }

}
