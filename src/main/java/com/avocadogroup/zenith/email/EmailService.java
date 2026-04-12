package com.avocadogroup.zenith.email;

import com.avocadogroup.zenith.common.configs.EmailConfig;
import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final EmailConfig emailConfig;
//    private final JavaMailSender mailSender;

    // Function to send a simple email using the mailSender (without attachment)
    public void sendEmail(SimpleEmailRequest request) {
        // Create the email message object (object that holds the email details)
        var message = new SimpleMailMessage();

        // Set the email details from the request
        message.setFrom("MS_qyXFGh@test-51ndgwvnpdnlzqx8.mlsender.net"); // TODO: Change this test domain to valid domain
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        // Try block to check for exceptions
        try {
            // Send the email
//            mailSender.send(message);

            System.out.println("Email sent successfully to " + request.getTo());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Function to send verification

    // TODO: Function to send email with attachment
//     public void sendEmailWithAttachment(CustomEmailRequest request) {}
}
