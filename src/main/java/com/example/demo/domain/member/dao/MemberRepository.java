package com.example.demo.domain.member.dao;

import com.example.demo.domain.member.model.Member;
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
public interface MemberRepository extends JpaRepository<Member, UUID> {
}
