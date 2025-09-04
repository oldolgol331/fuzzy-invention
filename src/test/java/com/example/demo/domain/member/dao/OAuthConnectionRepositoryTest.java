package com.example.demo.domain.member.dao;

import static com.example.demo.domain.common.util.TestUtils.createMember;
import static com.example.demo.domain.common.util.TestUtils.createOAuthConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import autoparams.AutoSource;
import autoparams.Repeat;
import com.example.demo.common.config.EnableJpaAuditingConfig;
import com.example.demo.common.config.P6SpyConfig;
import com.example.demo.common.config.QuerydslConfig;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.OAuthConnection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * PackageName : com.example.demo.domain.member.dao
 * FileName    : OAuthConnectionRepositoryTest
 * Author      : oldolgol331
 * Date        : 25. 9. 4.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 4.     oldolgol331          Initial creation
 */
@DataJpaTest
@Import({EnableJpaAuditingConfig.class, P6SpyConfig.class, QuerydslConfig.class})
class OAuthConnectionRepositoryTest {

    @Autowired
    private EntityManager             em;
    @Autowired
    private OAuthConnectionRepository oAuthConnectionRepository;

    @BeforeEach
    void setup() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE");
        //em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0");
        em.createNativeQuery("TRUNCATE TABLE members RESTART IDENTITY");
        em.createNativeQuery("TRUNCATE TABLE oauth_connections RESTART IDENTITY");
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE");
        //em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1");
    }

    @AfterEach
    void clear() {
        em.flush();
        em.clear();
    }

    @RepeatedTest(10)
    void save() {
        // given
        Member member = createMember();
        em.persist(member);
        OAuthConnection oAuthConnection = createOAuthConnection(member);

        // when
        Long id = oAuthConnectionRepository.save(oAuthConnection).getId();
        clear();

        // then
        OAuthConnection savedOAuthConnection = em.find(OAuthConnection.class, id);

        assertNotNull(savedOAuthConnection);
        assertEquals(oAuthConnection.getProvider(), savedOAuthConnection.getProvider());
        assertEquals(oAuthConnection.getProviderId(), savedOAuthConnection.getProviderId());
    }

    @RepeatedTest(10)
    void findById() {
        // given
        Member member = createMember();
        em.persist(member);
        OAuthConnection oAuthConnection = createOAuthConnection(member);
        em.persist(oAuthConnection);
        Long id = oAuthConnection.getId();
        clear();

        // when
        OAuthConnection findOAuthConnection = oAuthConnectionRepository.findById(id).get();

        // then
        assertNotNull(findOAuthConnection);
        assertEquals(oAuthConnection.getProvider(), findOAuthConnection.getProvider());
        assertEquals(oAuthConnection.getProviderId(), findOAuthConnection.getProviderId());
    }

    @ParameterizedTest
    @Repeat(10)
    @AutoSource
    void findById_unknownId(@Min(0) @Max(Long.MAX_VALUE) final long unknownId) {
        // when
        Optional<OAuthConnection> opOAuthConnection = oAuthConnectionRepository.findById(unknownId);

        // then
        assertFalse(opOAuthConnection.isPresent());
    }

    @RepeatedTest(10)
    void deleteById() {
        // given
        Member member = createMember();
        em.persist(member);
        OAuthConnection oAuthConnection = createOAuthConnection(member);
        em.persist(oAuthConnection);
        Long id = oAuthConnection.getId();
        clear();

        // when
        oAuthConnectionRepository.deleteById(id);

        // then
        OAuthConnection deletedOAuthConnection = em.find(OAuthConnection.class, id);

        assertNull(deletedOAuthConnection);
    }

}
