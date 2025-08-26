package com.example.demo.domain.post.controller;

import static com.example.demo.common.response.SuccessCode.POST_DELETE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.POST_LIKE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.POST_LIST_SEARCH_SUCCESS;
import static com.example.demo.common.response.SuccessCode.POST_READ_SUCCESS;
import static com.example.demo.common.response.SuccessCode.POST_UPDATE_SUCCESS;
import static com.example.demo.common.response.SuccessCode.POST_WRITE_SUCCESS;

import com.example.demo.common.response.ApiResponse;
import com.example.demo.common.response.SuccessCode;
import com.example.demo.common.response.annotation.CustomPageResponse;
import com.example.demo.common.security.model.CustomUserDetails;
import com.example.demo.common.util.PageableUtils;
import com.example.demo.domain.post.dto.PostRequest.PostCreateRequest;
import com.example.demo.domain.post.dto.PostRequest.PostUpdateRequest;
import com.example.demo.domain.post.dto.PostResponse.PostDetailResponse;
import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.post.service.PostViewCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
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
 * PackageName : com.example.demo.domain.post.controller
 * FileName    : PostController
 * Author      : oldolgol331
 * Date        : 25. 8. 26.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 26.    oldolgol331          Initial creation
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 작성, 수정, 조회/검색, 삭제 API를 제공합니다.")
public class PostController {

    private final PostService          postService;
    private final PostViewCountService postViewCountService;

    @PostMapping
    @Operation(summary = "게시글 작성", description = "새 게시글을 작성합니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final PostCreateRequest request
    ) {
        PostDetailResponse responseData = postService.createPost(userDetails.getId(), request);
        final SuccessCode  successCode  = POST_WRITE_SUCCESS;
        return ResponseEntity.status(successCode.getStatus())
                             .body(ApiResponse.success(successCode, responseData));
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> readPost(
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @PathVariable("id") @Min(1) final Long postId,
            final HttpServletRequest request
    ) {
        PostDetailResponse responseData = postService.getPostDetailById(postId, userDetails.getId());
        postViewCountService.incrementViewCount(postId, getClientIp(request));
        return ResponseEntity.ok(ApiResponse.success(POST_READ_SUCCESS, responseData));
    }

    @GetMapping
    @CustomPageResponse(
            numberOfElements = false,
            empty = false,
            hasContent = false
    )
    @Operation(summary = "게시글 목록 조회", description = "특정 키워드를 포함한 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Min(1) final int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) final int size,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt,DESC") final String sort,
            @RequestParam(value = "keyword", required = false) final String keyword
    ) {
        Sort                   orders       = PageableUtils.parseSort(sort);
        PageRequest            pageRequest  = PageRequest.of(page - 1, size, orders);
        Page<PostListResponse> responseData = postService.getPosts(keyword, pageRequest);
        return ResponseEntity.ok(ApiResponse.success(POST_LIST_SEARCH_SUCCESS, responseData));
    }

    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "특정 게시글의 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable("id") @Min(1) final Long postId,
            @AuthenticationPrincipal final CustomUserDetails userDetails,
            @Valid @RequestBody final PostUpdateRequest request
    ) {
        PostDetailResponse responseData = postService.updatePost(postId, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(POST_UPDATE_SUCCESS, responseData));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable("id") @Min(1) final Long postId,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        postService.deletePost(postId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(POST_DELETE_SUCCESS));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "게시글 좋아요 추가", description = "특정 게시글에 좋아요를 추가합니다.")
    public ResponseEntity<ApiResponse<PostDetailResponse>> addLike(
            @PathVariable("id") @Min(1) final Long postId,
            @AuthenticationPrincipal final CustomUserDetails userDetails
    ) {
        PostDetailResponse responseData = postService.addLike(postId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(POST_LIKE_SUCCESS, responseData));
    }

    // ========================= Private Methods =========================

    /**
     * 클라이언트의 요청으로부터 IP 주소를 추출합니다.
     *
     * @param request - HttpServletRequest
     * @return 클라이언트 IP
     */
    private String getClientIp(final HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return ip;
    }

}
