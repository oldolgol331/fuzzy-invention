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
import static com.example.demo.domain.common.util.TestUtils.FAKER;
import static com.example.demo.domain.common.util.TestUtils.createMember;
import static com.example.demo.domain.common.util.TestUtils.createMemberInfoResponse;
import static com.example.demo.domain.common.util.TestUtils.createMemberPasswordUpdateRequest;
import static com.example.demo.domain.common.util.TestUtils.createMemberSignUpRequest;
import static com.example.demo.domain.common.util.TestUtils.createMemberUpdateRequest;
import static com.example.demo.domain.common.util.TestUtils.createMemberWithdrawRequest;
import static com.example.demo.domain.common.util.TestUtils.createOAuthConnection;
import static com.example.demo.domain.common.util.TestUtils.createPassword;
import static com.example.demo.domain.member.constant.MemberConst.RATE_LIMIT_KEY_PREFIX;
import static com.example.demo.domain.member.constant.MemberConst.VERIFICATION_KEY_PREFIX;
import static com.example.demo.domain.member.model.MemberRole.USER;
import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;
import static com.example.demo.domain.member.model.MemberStatus.BLOCKED;
import static com.example.demo.domain.member.model.MemberStatus.DELETED;
import static com.example.demo.domain.member.model.MemberStatus.INACTIVE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
import com.example.demo.domain.member.service.properties.EmailProperties;
import com.example.demo.infra.redis.dao.RedisRepository;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PackageName : com.example.demo.domain.member.service
 * FileName    : MemberServiceTest
 * Author      : oldolgol331
 * Date        : 25. 9. 4.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 4.     oldolgol331          Initial creation
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl         memberService;
    @Mock
    private MemberRepository          memberRepository;
    @Mock
    private OAuthConnectionRepository oAuthConnectionRepository;
    @Mock
    private RedisRepository           redisRepository;
    @Mock
    private PasswordEncoder           passwordEncoder;
    @Mock
    private EmailService              emailService;
    @Mock
    private EmailProperties           emailProperties;

    @RepeatedTest(10)
    void signUpEmailUser() {
        // given
        MemberSignUpRequest request = createMemberSignUpRequest();

        String email    = request.getEmail().toLowerCase();
        String nickname = request.getNickname();
        String password = request.getPassword();

        when(memberRepository.existsByEmail(eq(email))).thenReturn(false);
        when(memberRepository.existsByNickname(eq(nickname))).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer((Answer<Member>) invocation -> {
            Member memberToSave = invocation.getArgument(0);
            setField(memberToSave, "id", UUID.randomUUID());
            return memberToSave;
        });
        when(passwordEncoder.encode(eq(password))).thenReturn(password);
        doNothing().when(redisRepository).setValue(anyString(), anyString(), any(Duration.class));
        doNothing().when(emailService).sendVerificationEmail(eq(email), anyString());

        // when
        MemberInfoResponse responseData = memberService.signUpEmailUser(request);

        // then
        assertEquals(email, responseData.getEmail());
        assertEquals(nickname, responseData.getNickname());
        assertEquals(USER, responseData.getRole());
        assertEquals(INACTIVE, responseData.getStatus());

        verify(memberRepository, times(1)).existsByEmail(eq(email));
        verify(memberRepository, times(1)).existsByNickname(eq(nickname));
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(passwordEncoder, times(1)).encode(eq(password));
        verify(redisRepository, times(1)).setValue(anyString(), anyString(), any(Duration.class));
        verify(emailService, times(1)).sendVerificationEmail(eq(email), anyString());
    }

    @RepeatedTest(10)
    void signUpEmailUser_passwordConfirmFailure() {
        // given
        MemberSignUpRequest request = createMemberSignUpRequest();

        request.setConfirmPassword(request.getPassword() + ".");

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.signUpEmailUser(request));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(PASSWORD_MISMATCH, exception.getErrorCode())
        );

        verify(memberRepository, never()).existsByEmail(anyString());
        verify(memberRepository, never()).existsByNickname(anyString());
        verify(memberRepository, never()).save(any(Member.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @RepeatedTest(10)
    void signUpEmailUser_existingEmail() {
        // given
        MemberSignUpRequest request = createMemberSignUpRequest();

        String email = request.getEmail().toLowerCase();

        when(memberRepository.existsByEmail(eq(email))).thenReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.signUpEmailUser(request));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(EMAIL_DUPLICATION, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).existsByEmail(eq(email));
        verify(memberRepository, never()).existsByNickname(anyString());
        verify(memberRepository, never()).save(any(Member.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @RepeatedTest(10)
    void signUpEmailUser_existingNickname() {
        // given
        MemberSignUpRequest request = createMemberSignUpRequest();

        String email    = request.getEmail().toLowerCase();
        String nickname = request.getNickname();

        when(memberRepository.existsByEmail(eq(email))).thenReturn(false);
        when(memberRepository.existsByNickname(eq(nickname))).thenReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.signUpEmailUser(request));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(NICKNAME_DUPLICATION, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).existsByEmail(eq(email));
        verify(memberRepository, times(1)).existsByNickname(eq(nickname));
        verify(memberRepository, never()).save(any(Member.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    @RepeatedTest(10)
    void findOrCreateMemberForOAuth_createNewMember() {
        // given
        String provider          = FAKER.company().name();
        String providerId        = UUID.randomUUID().toString();
        String emailFromOAuth    = FAKER.internet().emailAddress();
        String nicknameFromOAuth = FAKER.name().username().replace(".", "").substring(0, 5);

        Member member = Member.of(emailFromOAuth, nicknameFromOAuth, provider, providerId);
        setField(member, "id", UUID.randomUUID());
        OAuthConnection oAuthConnection = OAuthConnection.of(member, provider, providerId);

        when(memberRepository.findByProviderAndProviderId(eq(provider), eq(providerId))).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(eq(emailFromOAuth))).thenReturn(Optional.empty());
        when(memberRepository.existsByNickname(eq(nicknameFromOAuth))).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(oAuthConnectionRepository.save(any(OAuthConnection.class))).thenReturn(oAuthConnection);

        // when
        Member savedMember = memberService.findOrCreateMemberForOAuth(
                provider, providerId, emailFromOAuth, nicknameFromOAuth
        );

        // then
        assertEquals(emailFromOAuth, savedMember.getEmail());
        assertEquals(nicknameFromOAuth, savedMember.getNickname());
        assertEquals(provider, savedMember.getOAuthConnections().get(0).getProvider());
        assertEquals(providerId, savedMember.getOAuthConnections().get(0).getProviderId());

        verify(memberRepository, times(1)).findByProviderAndProviderId(eq(provider), eq(providerId));
        verify(memberRepository, times(1)).findByEmail(eq(emailFromOAuth));
        verify(memberRepository, times(1)).existsByNickname(eq(nicknameFromOAuth));
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(oAuthConnectionRepository, times(1)).save(any(OAuthConnection.class));
    }

    @RepeatedTest(10)
    void findOrCreateMemberForOAuth_connectExistingMember() {
        // given
        String provider          = FAKER.company().name();
        String providerId        = UUID.randomUUID().toString();
        String emailFromOAuth    = FAKER.internet().emailAddress();
        String nicknameFromOAuth = FAKER.name().username().replace(".", "").substring(0, 5);

        Member member = Member.of(emailFromOAuth, nicknameFromOAuth, provider, providerId);
        setField(member, "id", UUID.randomUUID());
        OAuthConnection oAuthConnection = OAuthConnection.of(member, provider, providerId);

        when(memberRepository.findByProviderAndProviderId(eq(provider), eq(providerId))).thenReturn(Optional.empty());
        when(memberRepository.findByEmail(eq(emailFromOAuth))).thenReturn(Optional.of(member));
        when(oAuthConnectionRepository.save(any(OAuthConnection.class))).thenReturn(oAuthConnection);

        // when
        Member memberToLink = memberService.findOrCreateMemberForOAuth(
                provider, providerId, emailFromOAuth, nicknameFromOAuth
        );

        // then
        assertEquals(emailFromOAuth, memberToLink.getEmail());
        assertEquals(nicknameFromOAuth, memberToLink.getNickname());
        assertEquals(provider, memberToLink.getOAuthConnections().get(0).getProvider());
        assertEquals(providerId, memberToLink.getOAuthConnections().get(0).getProviderId());

        verify(memberRepository, times(1)).findByProviderAndProviderId(eq(provider), eq(providerId));
        verify(memberRepository, times(1)).findByEmail(eq(emailFromOAuth));
        verify(oAuthConnectionRepository, times(1)).save(any(OAuthConnection.class));
        verify(memberRepository, never()).existsByNickname(eq(nicknameFromOAuth));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        Member member = Member.of(
                email,
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        setField(member, "id", UUID.randomUUID());

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(false);
        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));
        doNothing().when(emailService).sendVerificationEmail(eq(email), anyString());
        doNothing().when(redisRepository).setValue(anyString(), anyString(), any(Duration.class));

        // when
        memberService.resendVerificationEmail(email);

        // then
        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, times(1)).findByEmail(eq(email));
        verify(emailService, times(1)).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, times(2)).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail_limitCountOfRequests() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.resendVerificationEmail(email)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(TOO_MANY_REQUESTS, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, never()).findByEmail(eq(email));
        verify(emailService, never()).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail_notFoundMember() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(false);
        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.resendVerificationEmail(email)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, times(1)).findByEmail(eq(email));
        verify(emailService, never()).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail_invalidStatus_alreadyVerifiedEmail() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        Member member = Member.of(
                email,
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        setField(member, "id", UUID.randomUUID());

        member.setMemberStatus(ACTIVE);

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(false);
        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.resendVerificationEmail(email)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(ALREADY_VERIFIED_EMAIL, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, times(1)).findByEmail(eq(email));
        verify(emailService, never()).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail_invalidStatus_alreadyWithdrawn() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        Member member = Member.of(
                email,
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        setField(member, "id", UUID.randomUUID());

        member.setMemberStatus(DELETED);

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(false);
        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.resendVerificationEmail(email)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_ALREADY_WITHDRAWN, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, times(1)).findByEmail(eq(email));
        verify(emailService, never()).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void resendVerificationEmail_invalidStatus_blocked() {
        // given
        String email = FAKER.internet().emailAddress().toLowerCase();

        Member member = Member.of(
                email,
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        setField(member, "id", UUID.randomUUID());

        member.setMemberStatus(BLOCKED);

        when(redisRepository.hasKey(eq(RATE_LIMIT_KEY_PREFIX + email))).thenReturn(false);
        when(memberRepository.findByEmail(eq(email))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.resendVerificationEmail(email)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_BLOCKED, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).hasKey(eq(RATE_LIMIT_KEY_PREFIX + email));
        verify(memberRepository, times(1)).findByEmail(eq(email));
        verify(emailService, never()).sendVerificationEmail(eq(email), anyString());
        verify(redisRepository, never()).setValue(anyString(), anyString(), any(Duration.class));
    }

    @RepeatedTest(10)
    void verifyEmail() {
        // given
        String token = UUID.randomUUID().toString();

        Member member = Member.of(
                FAKER.internet().emailAddress().toLowerCase(),
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        UUID id = UUID.randomUUID();
        setField(member, "id", id);

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.of(id.toString()));
        when(memberRepository.findById(eq(id))).thenReturn(Optional.of(member));
        when(redisRepository.deleteData(eq(redisKey))).thenReturn(true);

        // when
        MemberInfoResponse responseData = memberService.verifyEmail(token);

        // then
        assertEquals(ACTIVE, responseData.getStatus());

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, times(1)).findById(eq(id));
        verify(redisRepository, times(1)).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void verifyEmail_invalidToken() {
        // given
        String token = UUID.randomUUID().toString();

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.verifyEmail(token));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(INVALID_VERIFICATION_TOKEN, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, never()).findById(any(UUID.class));
        verify(redisRepository, never()).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void verifyEmail_notFoundMember() {
        // given
        String token = UUID.randomUUID().toString();
        UUID   id    = UUID.randomUUID();

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.of(id.toString()));
        when(memberRepository.findById(eq(id))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.verifyEmail(token));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, times(1)).findById(eq(id));
        verify(redisRepository, never()).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void verifyEmail_invalidStatus_alreadyVerifiedEmail() {
        // given
        String token = UUID.randomUUID().toString();

        Member member = Member.of(
                FAKER.internet().emailAddress().toLowerCase(),
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        UUID id = UUID.randomUUID();
        setField(member, "id", id);

        member.setMemberStatus(ACTIVE);

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.of(id.toString()));
        when(memberRepository.findById(eq(id))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.verifyEmail(token));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(ALREADY_VERIFIED_EMAIL, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, times(1)).findById(eq(id));
        verify(redisRepository, never()).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void verifyEmail_invalidStatus_alreadyWithdrawn() {
        // given
        String token = UUID.randomUUID().toString();

        Member member = Member.of(
                FAKER.internet().emailAddress().toLowerCase(),
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        UUID id = UUID.randomUUID();
        setField(member, "id", id);

        member.setMemberStatus(DELETED);

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.of(id.toString()));
        when(memberRepository.findById(eq(id))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.verifyEmail(token));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_ALREADY_WITHDRAWN, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, times(1)).findById(eq(id));
        verify(redisRepository, never()).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void verifyEmail_invalidStatus_blocked() {
        // given
        String token = UUID.randomUUID().toString();

        Member member = Member.of(
                FAKER.internet().emailAddress().toLowerCase(),
                createPassword(),
                FAKER.name().username().replace(".", "").substring(0, 5)
        );
        UUID id = UUID.randomUUID();
        setField(member, "id", id);

        member.setMemberStatus(BLOCKED);

        String redisKey = VERIFICATION_KEY_PREFIX + token;

        when(redisRepository.getValue(eq(redisKey), eq(String.class))).thenReturn(Optional.of(id.toString()));
        when(memberRepository.findById(eq(id))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.verifyEmail(token));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_BLOCKED, exception.getErrorCode())
        );

        verify(redisRepository, times(1)).getValue(eq(redisKey), eq(String.class));
        verify(memberRepository, times(1)).findById(eq(id));
        verify(redisRepository, never()).deleteData(eq(redisKey));
    }

    @RepeatedTest(10)
    void getMemberInfoById() {
        // given
        UUID memberId = UUID.randomUUID();

        MemberInfoResponse memberInfoResponse = createMemberInfoResponse();

        when(memberRepository.getMemberInfoResponseByIdAndStatus(eq(memberId), eq(ACTIVE)))
                .thenReturn(memberInfoResponse);

        // when
        MemberInfoResponse responseData = memberService.getMemberInfoById(memberId);

        // then
        assertNotNull(responseData);
        assertEquals(memberInfoResponse.getId(), responseData.getId());
        assertEquals(memberInfoResponse.getEmail(), responseData.getEmail());
        assertEquals(memberInfoResponse.getNickname(), responseData.getNickname());
        assertEquals(memberInfoResponse.getRole(), responseData.getRole());
        assertEquals(memberInfoResponse.getStatus(), responseData.getStatus());
        assertEquals(memberInfoResponse.getProviders(), responseData.getProviders());
        assertEquals(memberInfoResponse.getCreatedAt(), responseData.getCreatedAt());
        assertEquals(memberInfoResponse.getUpdatedAt(), responseData.getUpdatedAt());

        verify(memberRepository, times(1)).getMemberInfoResponseByIdAndStatus(eq(memberId), eq(ACTIVE));
    }

    @RepeatedTest(10)
    void getMemberInfoById_notFoundMember() {
        // given
        UUID memberId = UUID.randomUUID();

        when(memberRepository.getMemberInfoResponseByIdAndStatus(eq(memberId), eq(ACTIVE))).thenReturn(null);

        // when
        MemberInfoResponse responseData = memberService.getMemberInfoById(memberId);

        // then
        assertNull(responseData);

        verify(memberRepository, times(1)).getMemberInfoResponseByIdAndStatus(eq(memberId), eq(ACTIVE));
    }

    @RepeatedTest(10)
    void updateMemberInfo() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberUpdateRequest request = createMemberUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(currentPassword), eq(member.getPassword()))).thenReturn(true);
        when(memberRepository.existsByNickname(eq(request.getNewNickname()))).thenReturn(false);

        // when
        MemberInfoResponse responseData = memberService.updateMemberInfo(memberId, request);

        // then
        assertNotNull(responseData);
        assertEquals(request.getNewNickname(), responseData.getNickname());

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(currentPassword), eq(member.getPassword()));
        verify(memberRepository, times(1)).existsByNickname(eq(request.getNewNickname()));
    }

    @RepeatedTest(10)
    void updateMemberInfo_notFoundMember() {
        // given
        UUID memberId = UUID.randomUUID();

        MemberUpdateRequest request = createMemberUpdateRequest(createPassword());

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.updateMemberInfo(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(memberRepository, never()).existsByNickname(eq(request.getNewNickname()));
    }

    @RepeatedTest(10)
    void updateMemberInfo_passwordMismatch() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String wrongPassword = member.getPassword() + ".";

        MemberUpdateRequest request = createMemberUpdateRequest(wrongPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(wrongPassword), eq(member.getPassword()))).thenReturn(false);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.updateMemberInfo(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(INVALID_PASSWORD, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(wrongPassword), eq(member.getPassword()));
        verify(memberRepository, never()).existsByNickname(eq(request.getNewNickname()));
    }

    @RepeatedTest(10)
    void updateMemberInfo_nicknameDuplicate() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberUpdateRequest request = createMemberUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(currentPassword), eq(member.getPassword()))).thenReturn(true);
        when(memberRepository.existsByNickname(eq(request.getNewNickname()))).thenReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.updateMemberInfo(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(NICKNAME_DUPLICATION, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(currentPassword), eq(member.getPassword()));
        verify(memberRepository, times(1)).existsByNickname(eq(request.getNewNickname()));
    }

    @RepeatedTest(10)
    void changePassword() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(true);
        when(passwordEncoder.encode(eq(request.getNewPassword()))).thenReturn(request.getNewPassword());

        // when
        memberService.changePassword(memberId, request);

        // then
        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, times(1)).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_passwordMismatch() {
        // given
        UUID memberId = UUID.randomUUID();

        String currentPassword = createPassword();

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword);
        request.setConfirmNewPassword(request.getNewPassword() + ".");

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(PASSWORD_MISMATCH, exception.getErrorCode())
        );

        verify(memberRepository, never()).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_notFoundMember() {
        // given
        UUID memberId = UUID.randomUUID();

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(createPassword());

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), anyString());
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_invalidStatus_inactive() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        String currentPassword = member.getPassword();

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_INACTIVE, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_invalidStatus_alreadyWithdrawn() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        String currentPassword = member.getPassword();
        member.setMemberStatus(DELETED);

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_ALREADY_WITHDRAWN, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_invalidStatus_blocked() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        String currentPassword = member.getPassword();
        member.setMemberStatus(BLOCKED);

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_BLOCKED, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void changePassword_invalidPassword() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberPasswordUpdateRequest request = createMemberPasswordUpdateRequest(currentPassword + ".");

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(false);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.changePassword(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(INVALID_PASSWORD, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
        verify(passwordEncoder, never()).encode(eq(request.getNewPassword()));
    }

    @RepeatedTest(10)
    void withdrawMember() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        createOAuthConnection(member);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        memberService.withdrawMember(memberId);

        // then
        assertEquals(DELETED, member.getMemberStatus());

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMember_notFoundMember() {
        // given
        UUID memberId = UUID.randomUUID();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.withdrawMember(memberId));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMember_invalidStatus_inactive() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.withdrawMember(memberId));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_INACTIVE, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMember_invalidStatus_alreadyWithdrawn() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(DELETED);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.withdrawMember(memberId));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_ALREADY_WITHDRAWN, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMember_invalidStatus_blocked() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(BLOCKED);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.withdrawMember(memberId));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_BLOCKED, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMember_emptyOAuthConnections() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> memberService.withdrawMember(memberId));

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(OAUTH_PROVIDER_NOT_SUPPORTED, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(true);

        // when
        memberService.withdrawMember(memberId, request);

        // then
        assertEquals(DELETED, member.getMemberStatus());

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword_notFoundMember() {
        // given
        UUID memberId = UUID.randomUUID();

        String currentPassword = createPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.withdrawMember(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_NOT_FOUND, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, never()).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword_invalidPassword() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(ACTIVE);
        String currentPassword = member.getPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword + ".");

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(false);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.withdrawMember(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(INVALID_PASSWORD, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword_invalidStatus_inactive() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        String currentPassword = member.getPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.withdrawMember(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_INACTIVE, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword_invalidStatus_alreadyWithdrawn() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(DELETED);
        String currentPassword = member.getPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.withdrawMember(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_ALREADY_WITHDRAWN, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

    @RepeatedTest(10)
    void withdrawMemberWithPassword_invalidStatus_blocked() {
        // given
        UUID memberId = UUID.randomUUID();

        Member member = createMember();
        setField(member, "id", memberId);
        member.setMemberStatus(BLOCKED);
        String currentPassword = member.getPassword();

        MemberWithdrawRequest request = createMemberWithdrawRequest(currentPassword);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(request.getCurrentPassword()), eq(currentPassword))).thenReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class, () -> memberService.withdrawMember(memberId, request)
        );

        // then
        assertAll(
                () -> assertNotNull(exception),
                () -> assertEquals(MEMBER_BLOCKED, exception.getErrorCode())
        );

        verify(memberRepository, times(1)).findById(eq(memberId));
        verify(passwordEncoder, times(1)).matches(eq(request.getCurrentPassword()), eq(currentPassword));
    }

}
