package com.example.demo.domain.member.service;

import static com.example.demo.common.response.ErrorCode.ALREADY_VERIFIED_EMAIL;
import static com.example.demo.common.response.ErrorCode.EMAIL_DUPLICATION;
import static com.example.demo.common.response.ErrorCode.INVALID_PASSWORD;
import static com.example.demo.common.response.ErrorCode.INVALID_VERIFICATION_TOKEN;
import static com.example.demo.common.response.ErrorCode.MEMBER_ALREADY_WITHDRAWN;
import static com.example.demo.common.response.ErrorCode.MEMBER_BLOCKED;
import static com.example.demo.common.response.ErrorCode.MEMBER_INACTIVE;
import static com.example.demo.common.response.ErrorCode.MEMBER_NOT_FOUND;
import static com.example.demo.common.response.ErrorCode.NICKNAME_DUPLICATION;
import static com.example.demo.common.response.ErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED;
import static com.example.demo.common.response.ErrorCode.PASSWORD_MISMATCH;
import static com.example.demo.common.response.ErrorCode.TOO_MANY_REQUESTS;
import static com.example.demo.domain.member.constant.MemberConst.RATE_LIMIT_KEY_PREFIX;
import static com.example.demo.domain.member.constant.MemberConst.VERIFICATION_KEY_PREFIX;
import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;
import static com.example.demo.domain.member.model.MemberStatus.BLOCKED;
import static com.example.demo.domain.member.model.MemberStatus.DELETED;
import static com.example.demo.domain.member.model.MemberStatus.INACTIVE;

import com.example.demo.common.error.CustomException;
import com.example.demo.common.mail.EmailService;
import com.example.demo.domain.member.dao.MemberRepository;
import com.example.demo.domain.member.dao.OAuthConnectionRepository;
import com.example.demo.domain.member.dto.MemberRequest.MemberPasswordUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberSignUpRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberWithdrawRequest;
import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.OAuthConnection;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PackageName : com.example.demo.domain.member.service
 * FileName    : MemberServiceImpl
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository          memberRepository;
    private final OAuthConnectionRepository oAuthConnectionRepository;
    private final RedisRepository           redisRepository;
    private final PasswordEncoder           passwordEncoder;
    private final EmailService              emailService;

    private final long   tokenExpiryMinutes;
    private final String verificationBaseUrl;

    public MemberServiceImpl(
            final MemberRepository memberRepository,
            final OAuthConnectionRepository oAuthConnectionRepository,
            final RedisRepository redisRepository,
            final PasswordEncoder passwordEncoder,
            final EmailService emailService,
            @Value("${email.verification.token-expiry-minutes}") final long tokenExpiryMinutes,
            @Value("${email.verification.base-url}") final String verificationBaseUrl
    ) {
        this.memberRepository = memberRepository;
        this.oAuthConnectionRepository = oAuthConnectionRepository;
        this.redisRepository = redisRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenExpiryMinutes = tokenExpiryMinutes;
        this.verificationBaseUrl = verificationBaseUrl;
    }

    /**
     * 회원 상태를 검증합니다. 활성화(ACTIVE) 상태가 아닌 경우 예외가 발생합니다.
     *
     * @param member - 회원
     */
    private static void memberStatusCheck(final Member member) {
        switch (member.getMemberStatus()) {
            case INACTIVE:
                throw new CustomException(MEMBER_INACTIVE);
            case DELETED:
                throw new CustomException(MEMBER_ALREADY_WITHDRAWN);
            case BLOCKED:
                throw new CustomException(MEMBER_BLOCKED);
            default:
                break;
        }
    }

    /**
     * 회원 가입 요청을 처리합니다. 이메일 인증을 위한 토큰을 생성하고 이메일을 발송합니다.
     *
     * @param request - 회원 가입 요청 DTO
     * @return 가입된 회원 정보 응답 DTO
     */
    @Transactional
    @Override
    public MemberInfoResponse signUpEmailUser(final MemberSignUpRequest request) {
        String lowerCaseEmail = request.getEmail().toLowerCase();

        if (!request.isPasswordConfirmed()) throw new CustomException(PASSWORD_MISMATCH);
        if (memberRepository.existsByEmail(lowerCaseEmail)) throw new CustomException(EMAIL_DUPLICATION);
        if (memberRepository.existsByNickname(request.getNickname())) throw new CustomException(NICKNAME_DUPLICATION);

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = memberRepository.save(
                Member.of(lowerCaseEmail, encodedPassword, request.getNickname())
        );

        sendVerificationEmailWithToken(member);

        return MemberInfoResponse.from(member);
    }

    /**
     * OAuth 제공자로부터 회원 정보를 저장하거나, 이미 존재하는 경우 기존 회원에 OAuth 연동을 추가합니다.
     *
     * @param provider          - OAuth 제공자
     * @param providerId        - OAuth 고유 식별자
     * @param emailFromOAuth    - OAuth 제공자로부터 받은 이메일
     * @param nicknameFromOAuth - OAuth 제공자로부터 받은 닉네임
     * @return 회원 엔티티
     */
    @Transactional
    @Override
    public Member findOrCreateMemberForOAuth(
            final String provider, final String providerId, final String emailFromOAuth, final String nicknameFromOAuth
    ) {
        Optional<Member> opMember = memberRepository.findByProviderAndProviderId(provider, providerId);
        if (opMember.isPresent()) return opMember.get();

        if (emailFromOAuth != null && !emailFromOAuth.trim().isEmpty()) {
            Optional<Member> opMemberFromEmail = memberRepository.findByEmail(emailFromOAuth);
            if (opMemberFromEmail.isPresent()) {
                Member memberToLink = opMemberFromEmail.get();
                oAuthConnectionRepository.save(OAuthConnection.of(memberToLink, provider, providerId));
                if (memberToLink.getMemberStatus() == INACTIVE) memberToLink.completeEmailVerification();
                return memberToLink;
            }
        }

        String newNickname = nicknameFromOAuth;
        if (memberRepository.existsByNickname(newNickname))
            newNickname = "user-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        Member savedMember = memberRepository.save(Member.of(emailFromOAuth, newNickname, provider, providerId));
        oAuthConnectionRepository.save(OAuthConnection.of(savedMember, provider, providerId));

        return savedMember;
    }

    /**
     * 비활성화(INACTIVE) 상태의 회원에게 인증 메일을 재발송합니다. 인증 메일 재발송 요청 반복을 1분간 금지합니다.
     *
     * @param email - 이메일 주소
     */
    @Transactional
    @Override
    public void resendVerificationEmail(final String email) {
        String lowerCaseEmail = email.toLowerCase();
        String rateLimitKey   = RATE_LIMIT_KEY_PREFIX + lowerCaseEmail;

        if (redisRepository.hasKey(rateLimitKey)) throw new CustomException(TOO_MANY_REQUESTS);

        Member member = memberRepository.findByEmail(lowerCaseEmail)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getMemberStatus() == ACTIVE) throw new CustomException(ALREADY_VERIFIED_EMAIL);
        if (member.getMemberStatus() == DELETED) throw new CustomException(MEMBER_ALREADY_WITHDRAWN);
        if (member.getMemberStatus() == BLOCKED) throw new CustomException(MEMBER_BLOCKED);

        sendVerificationEmailWithToken(member);

        redisRepository.setValue(rateLimitKey, "sent", Duration.ofMinutes(1));  // 1분간 재요청 금지
    }

    /**
     * 이메일 인증 토큰을 검증하고, 회원 상태를 활성화(ACTIVE)로 변경합니다.
     *
     * @param token - 이메일 인증 토큰
     * @return 인증된 회원 정보 응답 DTO
     */
    @Transactional
    @Override
    public MemberInfoResponse verifyEmail(final String token) {
        String redisKey = VERIFICATION_KEY_PREFIX + token;

        UUID memberId = UUID.fromString(
                redisRepository.getValue(redisKey, String.class)
                               .orElseThrow(() -> new CustomException(INVALID_VERIFICATION_TOKEN))
        );

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (member.getMemberStatus() == ACTIVE) throw new CustomException(ALREADY_VERIFIED_EMAIL);
        if (member.getMemberStatus() == DELETED) throw new CustomException(MEMBER_ALREADY_WITHDRAWN);
        if (member.getMemberStatus() == BLOCKED) throw new CustomException(MEMBER_BLOCKED);

        member.completeEmailVerification();
        redisRepository.deleteData(redisKey);

        return MemberInfoResponse.from(member);
    }

    /**
     * 회원 정보를 가져옵니다. 회원의 탈퇴일이 있는 경우 null을 반환합니다.
     *
     * @param memberId - 회원 ID
     * @return 조회된 회원 정보 응답 DTO
     */
    @Override
    public MemberInfoResponse getMemberInfoById(final UUID memberId) {
//        Member member = memberRepository.findById(memberId)
//                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
//        memberStatusCheck(member);
//        return MemberInfoResponse.from(member);
        return memberRepository.getMemberInfoResponseByIdAndStatus(memberId, ACTIVE);
    }

    /**
     * 회원 정보를 수정합니다. 비밀번호가 일치하는지 확인하고, 닉네임 중복 확인을 합니다.
     *
     * @param memberId - 회원 ID
     * @param request  - 회원 정보 수정 요청 DTO
     * @return 수정된 회원 정보 응답 DTO
     */
    @Transactional
    @Override
    public MemberInfoResponse updateMemberInfo(final UUID memberId, final MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        memberStatusCheck(member);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
            throw new CustomException(INVALID_PASSWORD);
        if (
                !member.getNickname().equals(request.getNewNickname())
                && memberRepository.existsByNickname(request.getNewNickname())
        )
            throw new CustomException(NICKNAME_DUPLICATION);

        member.setNickname(request.getNewNickname());

        return MemberInfoResponse.from(member);
    }

    /**
     * 회원 비밀번호를 변경합니다.
     *
     * @param memberId - 회원 ID
     * @param request  - 회원 비밀번호 변경 요청 DTO
     */
    @Transactional
    @Override
    public void changePassword(final UUID memberId, final MemberPasswordUpdateRequest request) {
        if (!request.isNewPasswordConfirmed()) throw new CustomException(PASSWORD_MISMATCH);

        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        memberStatusCheck(member);

        if (
                member.getPassword() == null ||
                !passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())
        )
            throw new CustomException(INVALID_PASSWORD);

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /**
     * OAuth 회원을 탈퇴 처리합니다. 회원의 탈퇴일을 현재 시간으로 설정하고, 회원 상태를 탈퇴 상태(DELETE)로 변경합니다.
     *
     * @param memberId - 회원 ID
     */
    @Transactional
    @Override
    public void withdrawMember(final UUID memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        memberStatusCheck(member);
        if (member.getOAuthConnections().isEmpty()) throw new CustomException(OAUTH_PROVIDER_NOT_SUPPORTED);
        member.withdraw();
    }

    /**
     * 이메일 회원을 탈퇴 처리합니다. 회원의 탈퇴일을 현재 시간으로 설정하고, 회원 상태를 탈퇴 상태(DELETE)로 변경합니다.
     *
     * @param memberId - 회원 ID
     * @param request  - 회원 탈퇴 요청 DTO
     */
    @Transactional
    @Override
    public void withdrawMember(final UUID memberId, final MemberWithdrawRequest request) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
            throw new CustomException(INVALID_PASSWORD);
        memberStatusCheck(member);
        member.withdraw();
    }

    // ========================= Private Methods =========================

    /**
     * 인증 메일을 발송합니다.
     *
     * @param member - 인증 대상 회원
     */
    private void sendVerificationEmailWithToken(final Member member) {
        String verificationToken = UUID.randomUUID().toString().replace("-", "");
        redisRepository.setValue(
                VERIFICATION_KEY_PREFIX + verificationToken,
                member.getId().toString(),
                Duration.ofMinutes(tokenExpiryMinutes)
        );

        String verificationLink = verificationBaseUrl + verificationToken;
        emailService.sendVerificationEmail(member.getEmail(), verificationLink);
    }

}
