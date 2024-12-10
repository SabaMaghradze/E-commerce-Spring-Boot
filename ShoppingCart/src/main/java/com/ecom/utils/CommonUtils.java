package com.ecom.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtils {

    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("test@gmail.com", "Shopping Cart");
        helper.setTo(recipientEmail);

        String content = "<p>Hello!</p>" + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password</p>" + "<p><a href=\"" + url
                + "\">Change my password</a></p>";

        helper.setSubject("Reset your password");
        helper.setText(content, true);
        mailSender.send(message);

        return true;
    }

    public String generateUrl(HttpServletRequest req) {
        String siteUrl = req.getRequestURL().toString();
        return siteUrl.replace(req.getServletPath(), "");
    }
}
