package com.hrplatform.backend.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final Resend resend;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.app-url}")
    private String appUrl;

    public EmailService(@Value("${RESEND_API_KEY}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Async
    public void sendInviteEmail(String toEmail, String candidateName, String assessmentTitle, String inviteToken) {
        String inviteUrl = appUrl + "/candidate/" + inviteToken;

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(from)
                .to(toEmail)
                .subject("You've been invited to complete an assessment: " + assessmentTitle)
                .html(buildHtml(candidateName, assessmentTitle, inviteUrl))
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            log.error("Failed to send invite email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildHtml(String candidateName, String assessmentTitle, String inviteUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: sans-serif; color: #1a1a1a; max-width: 560px; margin: 0 auto; padding: 32px 16px;">
                  <h2 style="margin-bottom: 8px;">Hi %s,</h2>
                  <p>You've been invited to complete the following assessment:</p>
                  <p style="font-size: 18px; font-weight: 600; margin: 24px 0;">%s</p>
                  <a href="%s"
                     style="display: inline-block; background: #2d6a4f; color: #fff; text-decoration: none;
                            padding: 12px 28px; border-radius: 6px; font-weight: 600;">
                    Start assessment
                  </a>
                  <p style="margin-top: 24px; font-size: 13px; color: #666;">
                    Or copy this link: <a href="%s">%s</a>
                  </p>
                </body>
                </html>
                """.formatted(candidateName, assessmentTitle, inviteUrl, inviteUrl, inviteUrl);
    }
}
