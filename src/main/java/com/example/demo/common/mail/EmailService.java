package com.example.demo.common.mail;

/**
 * PackageName : com.example.demo.common.mail
 * FileName    : EmailService
 * Author      : oldolgol331
 * Date        : 25. 8. 24.
 * Description :
 * =====================================================================================================================
 * DATE          AUTHOR               DESCRIPTION
 * ---------------------------------------------------------------------------------------------------------------------
 * 25. 8. 24.    oldolgol331          Initial creation
 */
public interface EmailService {

    void sendVerificationEmail(String toEmail, String verificationLink);

    void sendPasswordResetEmail(String toEmail, String resetLink);

}
