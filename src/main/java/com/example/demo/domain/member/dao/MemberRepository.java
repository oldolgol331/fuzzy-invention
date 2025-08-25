package com.example.demo.domain.member.dao;

import com.example.demo.common.config.annotation.DataDBJpaRepositoryMarker;
import com.example.demo.domain.member.model.Member;
import com.example.demo.domain.member.model.MemberStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.member.dao
 * FileName    : MemberRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@DataDBJpaRepositoryMarker
public interface MemberRepository extends JpaRepository<Member, UUID>, MemberRepositoryCustom {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByEmailAndMemberStatusAndDeletedAtNull(String email, MemberStatus memberStatus);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

}
