package com.example.demo.domain.member.dao;

import com.example.demo.domain.member.model.OAuthConnection;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName : com.example.demo.domain.member.dao
 * FileName    : OAuthConnectionRepository
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
public interface OAuthConnectionRepository extends JpaRepository<OAuthConnection, Long> {
}
