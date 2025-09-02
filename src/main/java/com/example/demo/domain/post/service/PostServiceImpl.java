package com.example.demo.domain.post.service;

import static com.example.demo.common.event.type.ChangeType.CREATED;
import static com.example.demo.common.event.type.ChangeType.DELETED;
import static com.example.demo.common.event.type.ChangeType.UPDATED;
import static com.example.demo.common.response.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.demo.common.response.ErrorCode.POST_LIKE_CANNOT;
import static com.example.demo.common.response.ErrorCode.POST_NOT_FOUND;
import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;

import com.example.demo.common.error.CustomException;
import com.example.demo.domain.member.dao.MemberRepository;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.post.dao.PostLikeRepository;
import com.example.demo.domain.post.dao.PostRepository;
import com.example.demo.domain.post.dto.PostRequest.PostCreateRequest;
import com.example.demo.domain.post.dto.PostRequest.PostUpdateRequest;
import com.example.demo.domain.post.dto.PostResponse.PostDetailResponse;
import com.example.demo.domain.post.dto.PostResponse.PostListResponse;
import com.example.demo.domain.post.event.event.PostChangedEvent;
import com.example.demo.domain.post.model.Post;
import com.example.demo.domain.post.model.PostLike;
import com.example.demo.domain.post.model.PostLikeId;
import com.example.demo.domain.post.service.PostCountServiceImpl.PostCountDto;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.example.demo.domain.post.service
 * FileName    : PostServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 25.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 25.    oldolgol331          Initial creation
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository            postRepository;
    private final PostLikeRepository        postLikeRepository;
    private final MemberRepository          memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    //private final PostSearchRepository      postSearchRepository;

    private final PostCountService postCountService;
    private final PostCacheService postCacheService;
    private final RedisRepository  redisRepository;

    /**
     * 새로운 게시글을 생성합니다.
     *
     * @param writerId - 작성자 ID
     * @param request  - 게시글 생성 요청 DTO
     * @return 생성된 게시글 상세 정보 응답 DTO
     */
    @Transactional
    @Override
    public PostDetailResponse createPost(final UUID writerId, final PostCreateRequest request) {
        Member writer = memberRepository.findByIdAndMemberStatus(writerId, ACTIVE)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Post savedPost = postRepository.save(Post.of(writer, request.getTitle(), request.getContent()));

        eventPublisher.publishEvent(PostChangedEvent.of(savedPost.getId(), CREATED));

        postCacheService.evictPostListCache();

        return PostDetailResponse.builder()
                                 .id(savedPost.getId())
                                 .writerId(writer.getId())
                                 .writer(writer.getNickname())
                                 .title(savedPost.getTitle())
                                 .content(savedPost.getContent())
                                 .viewCount(savedPost.getViewCount())
                                 .likeCount(savedPost.getLikeCount())
                                 .isDeleted(savedPost.getIsDeleted())
                                 .createdAt(savedPost.getCreatedAt())
                                 .updatedAt(savedPost.getUpdatedAt())
                                 .isWriter(true)
                                 .build();
    }

    /**
     * 게시글 상세 정보를 조회합니다.
     *
     * @param postId   - 게시글 ID
     * @param writerId - 작성자 ID
     * @param clientIp - 클라이언트 IP
     * @return 조회된 게시글 상세 정보 응답 DTO
     */
    @Override
    public PostDetailResponse getPostDetailById(final Long postId, final UUID writerId, final String clientIp) {
        PostDetailResponse response = postRepository.getPost(postId, writerId);
        postCountService.incrementViewCount(postId, clientIp);
        return response;
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param keyword  - 검색어
     * @param pageable - 페이징 정보
     * @return 게시글 페이징 목록 응답 DTO
     */
    @Override
    public Page<PostListResponse> getPosts(final String keyword, final Pageable pageable) {
        Page<PostListResponse> responsePage = postCacheService.getPosts(keyword, pageable);

        if (!responsePage.hasContent()) return responsePage;

        List<Long> postIdsOnCurrentPage = responsePage.getContent()
                                                      .stream()
                                                      .map(PostListResponse::getId)
                                                      .collect(Collectors.toList());

        Map<Long, PostCountDto> counts = postCountService.getViewCounts(postIdsOnCurrentPage);
        responsePage.getContent().forEach(
                response -> {
                    for (int i = 0; i < counts.size(); i++) {
                        response.setViewCount(counts.get(response.getId()).getViewCount());
                        response.setLikeCount(counts.get(response.getId()).getLikeCount());
                        response.setCommentCount(counts.get(response.getId()).getCommentCount());
                    }
                }
        );

        return responsePage;
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param postId   - 게시글 ID
     * @param writerId - 작성자 ID
     * @param request  - 게시글 수정 요청 DTO
     * @return 수정된 게시글 상세 정보 응답 DTO
     */
    @Transactional
    @Override
    public PostDetailResponse updatePost(final Long postId, final UUID writerId, final PostUpdateRequest request) {
        if (!memberRepository.existsByIdAndMemberStatus(writerId, ACTIVE)) throw new CustomException(MEMBER_NOT_FOUND);

        Post post = postRepository.findByIdAndWriterIdAndIsDeletedFalse(postId, writerId)
                                  .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        post.setTitle(request.getNewTitle());
        post.setContent(request.getNewContent());

        eventPublisher.publishEvent(PostChangedEvent.of(postId, UPDATED));

        postCacheService.evictPostListCache();

        return PostDetailResponse.builder()
                                 .id(post.getId())
                                 .writerId(post.getWriter().getId())
                                 .writer(post.getWriter().getNickname())
                                 .title(post.getTitle())
                                 .content(post.getContent())
                                 .viewCount(post.getViewCount())
                                 .likeCount(post.getLikeCount())
                                 .isDeleted(post.getIsDeleted())
                                 .createdAt(post.getCreatedAt())
                                 .updatedAt(post.getUpdatedAt())
                                 .isWriter(true)
                                 .build();
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId   - 게시글 ID
     * @param writerId - 작성자 ID
     */
    @Transactional
    @Override
    public void deletePost(final Long postId, final UUID writerId) {
        if (!memberRepository.existsByIdAndMemberStatus(writerId, ACTIVE)) throw new CustomException(MEMBER_NOT_FOUND);

        Post post = postRepository.findByIdAndWriterIdAndIsDeletedFalse(postId, writerId)
                                  .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        eventPublisher.publishEvent(PostChangedEvent.of(postId, DELETED));

        post.delete();

        postCacheService.evictPostListCache();
    }

    /**
     * 게시글에 좋아요를 추가하거나 추가된 좋아요를 취소합니다.
     *
     * @param postId   - 게시글 ID
     * @param memberId - 회원 ID
     * @return 게시글 상세 정보 응답 DTO
     */
    @Transactional
    @Override
    public PostDetailResponse likePost(final Long postId, final UUID memberId) {
        Member member = memberRepository.findByIdAndMemberStatus(memberId, ACTIVE)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId)
                                  .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        if (post.getWriter().getId().equals(memberId)) throw new CustomException(POST_LIKE_CANNOT);

        PostLikeId postLikeId = PostLikeId.builder().memberId(memberId).postId(postId).build();
        if (postLikeRepository.existsById(postLikeId)) {
            postLikeRepository.deleteById(postLikeId);
        } else {
            postLikeRepository.save(PostLike.of(member, post));
        }

        eventPublisher.publishEvent(PostChangedEvent.of(postId, UPDATED));

        return PostDetailResponse.builder()
                                 .id(post.getId())
                                 .writerId(post.getWriter().getId())
                                 .writer(post.getWriter().getNickname())
                                 .title(post.getTitle())
                                 .content(post.getContent())
                                 .viewCount(post.getViewCount())
                                 .likeCount(post.getLikeCount())
                                 .isDeleted(post.getIsDeleted())
                                 .createdAt(post.getCreatedAt())
                                 .updatedAt(post.getUpdatedAt())
                                 .isWriter(false)
                                 .build();
    }

}
