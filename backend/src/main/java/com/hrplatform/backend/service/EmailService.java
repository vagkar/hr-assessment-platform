package com.hrplatform.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.app-url}")
    private String appUrl;

    @Async
    public void sendInviteEmail(String toEmail, String candidateName, String assessmentTitle, String inviteToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("You've been invited to complete an assessment: " + assessmentTitle);

            String inviteUrl = appUrl + "/candidate/" + inviteToken;
            helper.setText(buildHtml(candidateName, assessmentTitle, inviteUrl), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // log but don't fail the invite creation
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
