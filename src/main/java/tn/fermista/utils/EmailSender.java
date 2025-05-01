package tn.fermista.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private static final String SMTP_HOST = "sandbox.smtp.mailtrap.io";
    private static final String SMTP_PORT = "2525";
    private static final String USERNAME = "b6fe749b4fd202";
    private static final String PASSWORD = "7a242d24e6ee74";

    public static void sendOTP(String recipientEmail, String otp) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@fermista.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP will expire in 10 minutes.");

        Transport.send(message);
    }
} 