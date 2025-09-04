package com.example.demo.domain.common.util;

import static lombok.AccessLevel.PRIVATE;

import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.OAuthConnection;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.instantiator.Instantiator;
import com.navercorp.fixturemonkey.api.introspector.BeanArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.javax.validation.plugin.JavaxValidationPlugin;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

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
                             .setLazy("password", () -> FAKER.internet().password())
                             .setLazy("nickname", () -> FAKER.name().username().replace(".", "").substring(0, 5))
                             .sampleList(size);
    }

    public static Member createMember() {
        return createMembers(1).get(0);
    }

    public static List<OAuthConnection> createOAuthConnections(final Member member, final int size) {
        return FIXTURE_MONKEY.giveMeBuilder(OAuthConnection.class)
                             .instantiate(
                                     Instantiator.factoryMethod("of")
                                                 .parameter(Member.class, "member")
                                                 .parameter(String.class, "provider")
                                                 .parameter(String.class, "providerId")
                             )
                             .set("member", member)
                             .setLazy("provider", () -> FAKER.company().name())
                             .setLazy("providerId", () -> UUID.randomUUID().toString())
                             .sampleList(size);
    }

    public static OAuthConnection createOAuthConnection(final Member member) {
        return createOAuthConnections(member, 1).get(0);
    }

}
