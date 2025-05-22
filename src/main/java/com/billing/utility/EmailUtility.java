package com.billing.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Slf4j
@Component
public class EmailUtility {
    private final String host;
    private final String port;
    private final String user;
    private final String password;
    @Autowired
    public EmailUtility(@Value("${email.host}") String host,
                        @Value("${email.port}") String port,
                        @Value("${email.user}") String user,
                        @Value("${email.password}") String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public void sendEmail(String to, String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            log.error("Error sending email to {}", to, e);
        }
    }
}
