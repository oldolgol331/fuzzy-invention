package com.example.demo.domain.member.constant;

import static lombok.AccessLevel.PRIVATE;

import java.util.regex.Pattern;
import lombok.NoArgsConstructor;

/**
 * PackageName : com.example.demo.domain.member.constant
 * FileName    : MemberConst
 * Author      : oldolgol331
 * Date        : 25. 8. 23.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 23.    oldolgol331          Initial creation
 */
@NoArgsConstructor(access = PRIVATE)
public class MemberConst {

    public static final String EMAIL_REGEX    = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[`~!@#$%^&*()-_=+[{]}|;:'\",<.>/?]).{8,20}$";
    public static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,15}$";

    public static final Pattern EMAIL_PATTERN    = Pattern.compile(EMAIL_REGEX);
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    public static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);

}
