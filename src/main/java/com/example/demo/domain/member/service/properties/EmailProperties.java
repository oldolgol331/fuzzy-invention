package com.example.demo.domain.member.service.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * PackageName : com.example.demo.domain.member.service.properties
 * FileName    : EmailProperties
 * Author      : oldolgol331
 * Date        : 25. 9. 5.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 9. 5.     oldolgol331          Initial creation
 */
@ConfigurationProperties(prefix = "email.verification")
@Getter
public class EmailProperties {

    private final long   tokenExpiryMinutes;
    private final String baseUrl;

    @ConstructorBinding
    public EmailProperties(final long tokenExpiryMinutes, final String baseUrl) {
        this.tokenExpiryMinutes = tokenExpiryMinutes;
        this.baseUrl = baseUrl;
    }

}
