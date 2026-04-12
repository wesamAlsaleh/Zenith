package com.avocadogroup.zenith.email;

import com.avocadogroup.zenith.email.dtos.SimpleEmailRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    //    private final EmailConfig emailConfig;
    private final JavaMailSender javaMailSender;

    private static final String NOREPLY_ADDRESS = "wesamalsaleh23@gmail.com";

    /**
     * Function to send a simple email using the mailSender (without attachment)
     *
     * @param request
     */
    public void sendEmail(SimpleEmailRequest request) {
        // Create the email message object (object that holds the email details)
        var message = new SimpleMailMessage();

        // Set the email details from the request
        message.setFrom(NOREPLY_ADDRESS);
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getBody());

        System.out.println("Sending email...");

        // Try block to check for exceptions
        try {
            log.info("Sending email..." + message);

            // Send the email
            javaMailSender.send(message);

            System.out.println("Email sent successfully to " + request.getTo());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Function to send verification

    // TODO: Function to send email with attachment
//     public void sendEmailWithAttachment(CustomEmailRequest request) {}
}
