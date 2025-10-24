package com.schoolmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:#{null}}")
    private String fromEmail;

    /**
     * Send simple text email
     */
    @Transactional
    public boolean sendSimpleEmail(String to, String subject, String body) {
        try {
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                log.error("Email configuration not set. Please configure spring.mail.username");
                return false;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Error sending simple email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send HTML email
     */
    @Transactional
    public boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Error sending HTML email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Send bulk emails to multiple recipients
     */
    @Transactional
    public int sendBulkEmails(List<String> recipients, String subject, String body, boolean isHtml) {
        int successCount = 0;
        for (String recipient : recipients) {
            boolean success = isHtml ? 
                sendHtmlEmail(recipient, subject, body) : 
                sendSimpleEmail(recipient, subject, body);
            if (success) successCount++;
        }
        log.info("Bulk email sent to {}/{} recipients", successCount, recipients.size());
        return successCount;
    }

    /**
     * Send notification to teachers about upcoming classes
     */
    @Transactional
    public boolean sendClassReminderToTeachers(List<String> teacherEmails, String className, String subject, LocalDateTime classTime) {
        String subjectLine = "Class Reminder - " + className;
        String body = String.format(
            "Dear Teacher,\n\n" +
            "This is a reminder that you have a class scheduled:\n\n" +
            "Class: %s\n" +
            "Subject: %s\n" +
            "Time: %s\n\n" +
            "Please ensure you are prepared and arrive on time.\n\n" +
            "Best regards,\nSchool Management System",
            className, subject, classTime.toString()
        );

        int successCount = 0;
        for (String email : teacherEmails) {
            if (sendSimpleEmail(email, subjectLine, body)) {
                successCount++;
            }
        }
        log.info("Class reminders sent to {}/{} teachers", successCount, teacherEmails.size());
        return successCount > 0;
    }

    /**
     * Send exam reminder to students
     */
    @Transactional
    public boolean sendExamReminderToStudents(List<String> studentEmails, String examName, String subject, LocalDateTime examTime) {
        String subjectLine = "Exam Reminder - " + examName;
        String body = String.format(
            "Dear Student,\n\n" +
            "This is a reminder about your upcoming exam:\n\n" +
            "Exam: %s\n" +
            "Subject: %s\n" +
            "Time: %s\n\n" +
            "Please ensure you are well prepared and arrive on time.\n\n" +
            "Best regards,\nSchool Management System",
            examName, subject, examTime.toString()
        );

        int successCount = 0;
        for (String email : studentEmails) {
            if (sendSimpleEmail(email, subjectLine, body)) {
                successCount++;
            }
        }
        log.info("Exam reminders sent to {}/{} students", successCount, studentEmails.size());
        return successCount > 0;
    }

    /**
     * Send fee balance reminder to parents
     */
    @Transactional
    public boolean sendFeeReminderToParents(List<String> parentEmails, String studentName, Double balanceAmount) {
        String subjectLine = "Fee Balance Reminder - " + studentName;
        String body = String.format(
            "Dear Parent,\n\n" +
            "This is a reminder about the outstanding fee balance for your child:\n\n" +
            "Student: %s\n" +
            "Outstanding Balance: KES %.2f\n\n" +
            "Please make payment at your earliest convenience to avoid any inconvenience.\n\n" +
            "Best regards,\nSchool Management System",
            studentName, balanceAmount
        );

        int successCount = 0;
        for (String email : parentEmails) {
            if (sendSimpleEmail(email, subjectLine, body)) {
                successCount++;
            }
        }
        log.info("Fee reminders sent to {}/{} parents", successCount, parentEmails.size());
        return successCount > 0;
    }

    /**
     * Send school closure/opening notifications
     */
    @Transactional
    public boolean sendSchoolClosureNotification(List<String> allEmails, String message, LocalDateTime closureDate, LocalDateTime reopeningDate) {
        String subjectLine = "School Closure/Opening Notice";
        String body = String.format(
            "Dear School Community,\n\n" +
            "%s\n\n" +
            "Closure Date: %s\n" +
            "Reopening Date: %s\n\n" +
            "Please plan accordingly and stay safe.\n\n" +
            "Best regards,\nSchool Management",
            message, closureDate.toString(), reopeningDate.toString()
        );

        int successCount = 0;
        for (String email : allEmails) {
            if (sendSimpleEmail(email, subjectLine, body)) {
                successCount++;
            }
        }
        log.info("School closure notifications sent to {}/{} users", successCount, allEmails.size());
        return successCount > 0;
    }

    /**
     * Send welcome email to new users
     */
    @Transactional
    public boolean sendWelcomeEmail(String to, String name, String role) {
        String subjectLine = "Welcome to School Management System";
        String body = String.format(
            "Dear %s,\n\n" +
            "Welcome to our School Management System!\n\n" +
            "Your account has been created with the role: %s\n" +
            "You can now access the system using your credentials.\n\n" +
            "If you have any questions, please contact the administration.\n\n" +
            "Best regards,\nSchool Management System",
            name, role
        );

        return sendSimpleEmail(to, subjectLine, body);
    }

    /**
     * Send password reset email
     */
    @Transactional
    public boolean sendPasswordResetEmail(String to, String name, String resetToken) {
        String subjectLine = "Password Reset Request";
        String body = String.format(
            "Dear %s,\n\n" +
            "You have requested to reset your password.\n\n" +
            "Reset Token: %s\n\n" +
            "Please use this token to reset your password. If you did not request this, please ignore this email.\n\n" +
            "Best regards,\nSchool Management System",
            name, resetToken
        );

        return sendSimpleEmail(to, subjectLine, body);
    }
}
