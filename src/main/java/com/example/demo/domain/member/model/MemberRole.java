package com.example.demo.domain.member.model;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * PackageName : com.example.demo.domain.member.model
 * FileName    : MemberRole
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MemberRole {

    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String roleValue;

}
