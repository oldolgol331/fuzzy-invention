package com.example.demo.domain.common.util;

import static com.example.demo.domain.member.constant.MemberConst.PASSWORD_PATTERN;
import static com.example.demo.domain.member.model.MemberRole.ADMIN;
import static com.example.demo.domain.member.model.MemberRole.USER;
import static com.example.demo.domain.member.model.MemberStatus.ACTIVE;
import static java.util.concurrent.TimeUnit.DAYS;
import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.member.dto.MemberRequest.MemberPasswordUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberSignUpRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberUpdateRequest;
import com.example.demo.domain.member.dto.MemberRequest.MemberWithdrawRequest;
import com.example.demo.domain.member.dto.MemberResponse.MemberInfoResponse;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberRole;
import com.example.demo.domain.member.model.MemberStatus;
import com.example.demo.domain.member.model.OAuthConnection;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.instantiator.Instantiator;
import com.navercorp.fixturemonkey.api.introspector.BeanArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;
import net.jqwik.api.Arbitraries;

/**
 * PackageName : com.example.demo.domain.common.util
 * FileName    : TestUtils
 * Author      : oldolgol331
 * Date        : 25. 9. 4.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 4.     oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public abstract class TestUtils {

    public static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey.builder()
                                                                    .objectIntrospector(
                                                                            new FailoverIntrospector(
                                                                                    Arrays.asList(
                                                                                            ConstructorPropertiesArbitraryIntrospector.INSTANCE,
                                                                                            BuilderArbitraryIntrospector.INSTANCE,
                                                                                            FieldReflectionArbitraryIntrospector.INSTANCE,
                                                                                            BeanArbitraryIntrospector.INSTANCE
                                                                                    ),
                                                                                    false
                                                                            )
                                                                    )
                                                                    .plugin(new JavaxValidationPlugin())
                                                                    .defaultNotNull(true)
                                                                    .nullableContainer(false)
                                                                    .nullableElement(false)
                                                                    .build();

    public static final Faker FAKER = new Faker(new Locale("en"), new Random());

    public static List<Member> createMembers(final int size) {
        return FIXTURE_MONKEY.giveMeBuilder(Member.class)
                             .instantiate(
                                     Instantiator.factoryMethod("of")
                                                 .parameter(String.class, "email")
                                                 .parameter(String.class, "password")
                                                 .parameter(String.class, "nickname")
                             )
                             .setLazy("email", () -> FAKER.internet().emailAddress())
                             .setLazy("password", TestUtils::createPassword)
                             .setLazy("nickname", () -> FAKER.name().username().replace(".", "").substring(0, 5))
                             .sampleList(size);
    }

    public static Member createMember() {
        return createMembers(1).get(0);
    }

    public static List<OAuthConnection> createOAuthConnections(final Member member, final int size) {
        return IntStream.range(0, size)
                        .mapToObj(
                                i -> OAuthConnection.of(
                                        member, FAKER.company().name(), UUID.randomUUID().toString()
                                )
                        )
                        .collect(Collectors.toList());
    }

    public static OAuthConnection createOAuthConnection(final Member member) {
        return createOAuthConnections(member, 1).get(0);
    }

    public static MemberSignUpRequest createMemberSignUpRequest() {
        String password = createPassword();
        return FIXTURE_MONKEY.giveMeBuilder(MemberSignUpRequest.class)
                             .instantiate(
                                     Instantiator.constructor()
                                                 .parameter(String.class, "email")
                                                 .parameter(String.class, "password")
                                                 .parameter(String.class, "confirmPassword")
                                                 .parameter(String.class, "nickname")
                             )
                             .setLazy("email", () -> FAKER.internet().emailAddress())
                             .set("password", password)
                             .set("confirmPassword", password)
                             .setLazy("nickname", () -> FAKER.name().username().replace(".", "").substring(0, 5))
                             .sample();
    }

    public static MemberInfoResponse createMemberInfoResponse() {
        return FIXTURE_MONKEY.giveMeBuilder(MemberInfoResponse.class)
                             .instantiate(
                                     Instantiator.constructor()
                                                 .parameter(UUID.class, "id")
                                                 .parameter(String.class, "email")
                                                 .parameter(String.class, "nickname")
                                                 .parameter(MemberRole.class, "role")
                                                 .parameter(MemberStatus.class, "status")
                                                 .parameter(List.class, "providers")
                                                 .parameter(LocalDateTime.class, "createdAt")
                                                 .parameter(LocalDateTime.class, "updatedAt")
                             )
                             .setLazy("id", UUID::randomUUID)
                             .setLazy("email", () -> FAKER.internet().emailAddress())
                             .setLazy("nickname", () -> FAKER.name().username().replace(".", "").substring(0, 5))
                             .set("role", Arbitraries.of(USER, ADMIN))
                             .set("status", ACTIVE)
                             .set("providers", Collections.emptyList())
                             .setLazy("createdAt", () -> FAKER.date().past(365, DAYS).toLocalDateTime())
                             .setLazy("updatedAt", () -> FAKER.date().past(365, DAYS).toLocalDateTime())
                             .sample();
    }

    public static MemberUpdateRequest createMemberUpdateRequest(final String currentPassword) {
        return FIXTURE_MONKEY.giveMeBuilder(MemberUpdateRequest.class)
                             .instantiate(
                                     Instantiator.constructor()
                                                 .parameter(String.class, "newNickname")
                                                 .parameter(String.class, "currentPassword")
                             )
                             .setLazy(
                                     "newNickname",
                                     () -> "updated" + FAKER.name().username().replace(".", "").substring(0, 5)
                             )
                             .set("currentPassword", currentPassword)
                             .sample();
    }

    public static MemberPasswordUpdateRequest createMemberPasswordUpdateRequest(final String currentPassword) {
        String newPassword = createPassword();
        return FIXTURE_MONKEY.giveMeBuilder(MemberPasswordUpdateRequest.class)
                             .instantiate(
                                     Instantiator.constructor()
                                                 .parameter(String.class, "newPassword")
                                                 .parameter(String.class, "confirmNewPassword")
                                                 .parameter(String.class, "currentPassword")
                             )
                             .set("newPassword", newPassword)
                             .set("confirmNewPassword", newPassword)
                             .set("currentPassword", currentPassword)
                             .sample();
    }

    public static MemberWithdrawRequest createMemberWithdrawRequest(final String currentPassword) {
        return new MemberWithdrawRequest(currentPassword);
    }

    public static String createPassword() {
        String password;
        do {
            password = FAKER.internet().password(8, 20, true, true);
        } while (!PASSWORD_PATTERN.matcher(password).matches());
        return password;
    }

}
