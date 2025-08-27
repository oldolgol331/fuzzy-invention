package com.example.demo.domain.post.service;

import com.example.demo.domain.post.dto.PostRequest.PostCreateRequest;
import com.example.demo.domain.post.dto.PostRequest.PostUpdateRequest;
import com.example.demo.domain.post.dto.PostResponse.PostDetailResponse;
import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostService
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
public interface PostService {

    PostDetailResponse createPost(UUID writerId, PostCreateRequest request);

    PostDetailResponse getPostDetailById(Long postId, UUID writerId);

    Page<PostListResponse> getPosts(String keyword, Pageable pageable);

    PostDetailResponse updatePost(Long postId, UUID writerId, PostUpdateRequest request);

    void deletePost(Long postId, UUID writerId);

    PostDetailResponse likePost(Long postId, UUID memberId);

}
