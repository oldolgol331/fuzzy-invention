package com.example.demo.domain.member.dao;

import static com.example.demo.domain.common.util.TestUtils.createMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import autoparams.AutoSource;
import autoparams.Repeat;
import com.example.demo.common.config.EnableJpaAuditingConfig;
import com.example.demo.common.config.P6SpyConfig;
import com.example.demo.common.config.QuerydslConfig;
import com.example.demo.domain.member.model.Member;
import java.util.Optional;
import java.util.UUID;
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
 * FileName    : MemberRepositoryTest
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
class MemberRepositoryTest {

    @Autowired
    private EntityManager    em;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE");
        //em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0");
        em.createNativeQuery("TRUNCATE TABLE members RESTART IDENTITY");
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

        // when
        UUID id = memberRepository.save(member).getId();
        clear();

        // then
        Member savedMember = em.find(Member.class, id);

        assertNotNull(savedMember);
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getNickname(), savedMember.getNickname());
    }

    @RepeatedTest(10)
    void findById() {
        // given
        Member member = createMember();
        em.persist(member);
        UUID id = member.getId();
        clear();

        // when
        Member findMember = memberRepository.findById(id).get();

        // then
        assertNotNull(findMember);
        assertEquals(member.getEmail(), findMember.getEmail());
        assertEquals(member.getPassword(), findMember.getPassword());
        assertEquals(member.getNickname(), findMember.getNickname());
    }

    @ParameterizedTest
    @Repeat(10)
    @AutoSource
    void findById_unknownId(final UUID unknownId) {
        // when
        Optional<Member> opMember = memberRepository.findById(unknownId);

        // then
        assertFalse(opMember.isPresent());
    }

    @RepeatedTest(10)
    void update() {
        // given
        Member member = createMember();
        em.persist(member);
        UUID id = member.getId();
        clear();

        // when
        Member findMember = memberRepository.findById(id).get();
        findMember.setNickname("updated" + findMember.getNickname());
        findMember.setPassword("updated" + findMember.getPassword());
        clear();

        // then
        Member updatedMember = em.find(Member.class, id);

        assertNotNull(updatedMember);
        assertEquals(findMember.getNickname(), updatedMember.getNickname());
        assertNotEquals(member.getNickname(), updatedMember.getNickname());
        assertEquals(findMember.getPassword(), updatedMember.getPassword());
        assertNotEquals(member.getPassword(), updatedMember.getPassword());
    }

    @RepeatedTest(10)
    void deleteById() {
        // given
        Member member = createMember();
        em.persist(member);
        UUID id = member.getId();
        clear();

        // when
        memberRepository.deleteById(id);

        // then
        Member deletedMember = em.find(Member.class, id);

        assertNull(deletedMember);
    }

}
