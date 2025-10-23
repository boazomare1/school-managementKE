package com.schoolmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Generate HTML email for class reminders
     */
    public String generateClassReminderHtml(String teacherName, String subject, String className, 
                                           String room, LocalDateTime startTime) {
        Context context = new Context();
        context.setVariable("teacherName", teacherName);
        context.setVariable("subject", subject);
        context.setVariable("className", className);
        context.setVariable("room", room);
        context.setVariable("startTime", startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("date", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        return templateEngine.process("class-reminder", context);
    }

    /**
     * Generate HTML email for exam reminders
     */
    public String generateExamReminderHtml(String studentName, String examName, String subject, 
                                         LocalDateTime examDate, int duration) {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("examName", examName);
        context.setVariable("subject", subject);
        context.setVariable("examDate", examDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        context.setVariable("examTime", examDate.format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("duration", duration);
        
        return templateEngine.process("exam-reminder", context);
    }

    /**
     * Generate HTML email for fee reminders
     */
    public String generateFeeReminderHtml(String parentName, String studentName, double outstandingAmount, 
                                        String dueDate, String invoiceNumber) {
        Context context = new Context();
        context.setVariable("parentName", parentName);
        context.setVariable("studentName", studentName);
        context.setVariable("outstandingAmount", String.format("%.2f", outstandingAmount));
        context.setVariable("dueDate", dueDate);
        context.setVariable("invoiceNumber", invoiceNumber);
        
        return templateEngine.process("fee-reminder", context);
    }

    /**
     * Generate HTML email for attendance notifications
     */
    public String generateAttendanceNotificationHtml(String parentName, String studentName, 
                                                   String className, String status, String date) {
        Context context = new Context();
        context.setVariable("parentName", parentName);
        context.setVariable("studentName", studentName);
        context.setVariable("className", className);
        context.setVariable("status", status);
        context.setVariable("date", date);
        
        return templateEngine.process("attendance-notification", context);
    }

    /**
     * Generate HTML email for school closure notifications
     */
    public String generateSchoolClosureHtml(String reason, String closureDate, String reopeningDate) {
        Context context = new Context();
        context.setVariable("reason", reason);
        context.setVariable("closureDate", closureDate);
        context.setVariable("reopeningDate", reopeningDate);
        
        return templateEngine.process("school-closure", context);
    }

    /**
     * Generate HTML email for grade notifications
     */
    public String generateGradeNotificationHtml(String parentName, String studentName, String className,
                                             String subject, String grade, String remarks) {
        Context context = new Context();
        context.setVariable("parentName", parentName);
        context.setVariable("studentName", studentName);
        context.setVariable("className", className);
        context.setVariable("subject", subject);
        context.setVariable("grade", grade);
        context.setVariable("remarks", remarks);
        
        return templateEngine.process("grade-notification", context);
    }

    /**
     * Generate HTML email for assignment notifications
     */
    public String generateAssignmentNotificationHtml(String studentName, String assignmentTitle, 
                                                   String description, String dueDate) {
        Context context = new Context();
        context.setVariable("studentName", studentName);
        context.setVariable("assignmentTitle", assignmentTitle);
        context.setVariable("description", description);
        context.setVariable("dueDate", dueDate);
        
        return templateEngine.process("assignment-notification", context);
    }

    /**
     * Generate HTML email for welcome notifications
     */
    public String generateWelcomeHtml(String name, String role, String schoolName) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("role", role);
        context.setVariable("schoolName", schoolName);
        
        return templateEngine.process("welcome-email", context);
    }

    /**
     * Generate HTML email for password reset
     */
    public String generatePasswordResetHtml(String name, String resetToken, String resetUrl) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetToken", resetToken);
        context.setVariable("resetUrl", resetUrl);
        
        return templateEngine.process("password-reset", context);
    }
}

