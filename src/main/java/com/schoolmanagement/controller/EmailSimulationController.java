package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.service.SimpleEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email/simulation")
@RequiredArgsConstructor
@Slf4j
public class EmailSimulationController {

    private final SimpleEmailService emailService;

    // User email addresses
    private static final String PARENT_EMAIL = "leniobocyber@gmail.com";
    private static final String TEACHER_EMAIL = "nyabigena@gmail.com";
    private static final String STUDENT_EMAIL = "nyaramba82@gmail.com";
    private static final String ADMIN_EMAIL = "omareboaz1@gmail.com";

    /**
     * Simulate all parent notifications
     */
    @PostMapping("/parent-notifications")
    public ResponseEntity<ApiResponse<String>> simulateParentNotifications() {
        try {
            log.info("🎯 Starting Parent Notification Simulation for: {}", PARENT_EMAIL);
            
            // 1. Fee Balance Reminder
            String feeSubject = "💰 Fee Balance Reminder - Alexina Johnson";
            String feeMessage = String.format(
                "Dear Parent,\n\n" +
                "This is a reminder about the outstanding fee balance for your child:\n\n" +
                "Student: Alexina Johnson\n" +
                "Class: Form 2A\n" +
                "Outstanding Balance: KES 15,500.00\n" +
                "Due Date: %s\n" +
                "Invoice Number: INV-2025-001234\n\n" +
                "Please make payment at your earliest convenience to avoid any inconvenience.\n\n" +
                "Payment Options:\n" +
                "• M-Pesa: Paybill 123456, Account: Alexina Johnson\n" +
                "• Bank Transfer: Account details available at school\n" +
                "• Cash payment at school office\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(PARENT_EMAIL, feeSubject, feeMessage);
            log.info("✅ Fee reminder sent to parent");

            // 2. Attendance Notification (Absent)
            String attendanceSubject = "📊 Daily Attendance Report - Alexina Johnson";
            String attendanceMessage = String.format(
                "Dear Parent,\n\n" +
                "This is to inform you about your child's attendance today:\n\n" +
                "Student: Alexina Johnson\n" +
                "Class: Form 2A\n" +
                "Date: %s\n" +
                "Status: ❌ ABSENT\n\n" +
                "Your child was absent from school today. Please contact the school if this was unexpected.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(PARENT_EMAIL, attendanceSubject, attendanceMessage);
            log.info("✅ Attendance notification sent to parent");

            // 3. Grade Notification
            String gradeSubject = "📈 Grade Update - Alexina Johnson";
            String gradeMessage = String.format(
                "Dear Parent,\n\n" +
                "This is to inform you about your child's academic performance:\n\n" +
                "Student: Alexina Johnson\n" +
                "Class: Form 2A\n" +
                "Subject: Mathematics\n" +
                "Grade: A-\n" +
                "Remarks: Excellent performance! Keep up the good work.\n\n" +
                "This is to inform you about your child's academic performance in the above subject.\n\n" +
                "Please continue to support your child's learning journey.\n\n" +
                "Best regards,\nSchool Management System"
            );
            emailService.sendSimpleEmail(PARENT_EMAIL, gradeSubject, gradeMessage);
            log.info("✅ Grade notification sent to parent");

            // 4. School Closure Notification
            String closureSubject = "🏫 School Closure Notice";
            String closureMessage = String.format(
                "Dear Parent,\n\n" +
                "This is to inform you about a school closure:\n\n" +
                "Reason: National Holiday - Mashujaa Day\n" +
                "Closure Date: %s\n" +
                "Reopening Date: %s\n\n" +
                "Please plan accordingly and ensure your child's safety during this period.\n\n" +
                "Best regards,\nSchool Management",
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
            emailService.sendSimpleEmail(PARENT_EMAIL, closureSubject, closureMessage);
            log.info("✅ School closure notification sent to parent");

            return ResponseEntity.ok(ApiResponse.success("All parent notifications sent successfully to " + PARENT_EMAIL));
        } catch (Exception e) {
            log.error("Error sending parent notifications: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send parent notifications: " + e.getMessage()));
        }
    }

    /**
     * Simulate all teacher notifications
     */
    @PostMapping("/teacher-notifications")
    public ResponseEntity<ApiResponse<String>> simulateTeacherNotifications() {
        try {
            log.info("🎯 Starting Teacher Notification Simulation for: {}", TEACHER_EMAIL);
            
            // 1. Class Reminder
            String classSubject = "📚 Class Reminder - Mathematics";
            String classMessage = String.format(
                "Dear Teacher,\n\n" +
                "You have a class scheduled in 15 minutes:\n\n" +
                "Subject: Mathematics\n" +
                "Class: Form 2A\n" +
                "Time: %s\n" +
                "Room: Room 15\n" +
                "Date: %s\n\n" +
                "Please ensure you are prepared and arrive on time.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusMinutes(15).format(DateTimeFormatter.ofPattern("HH:mm")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(TEACHER_EMAIL, classSubject, classMessage);
            log.info("✅ Class reminder sent to teacher");

            // 2. Exam Reminder
            String examSubject = "📝 Exam Reminder - Form 2A Mathematics";
            String examMessage = String.format(
                "Dear Teacher,\n\n" +
                "This is a reminder about an upcoming exam you are supervising:\n\n" +
                "Exam: Mid-Term Mathematics Exam\n" +
                "Class: Form 2A\n" +
                "Date: %s\n" +
                "Time: 09:00 AM\n" +
                "Duration: 2 hours\n" +
                "Room: Room 15\n\n" +
                "Please ensure you are present 15 minutes before the exam starts.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(TEACHER_EMAIL, examSubject, examMessage);
            log.info("✅ Exam reminder sent to teacher");

            // 3. Assignment Submission Reminder
            String assignmentSubject = "📋 Assignment Submission Reminder";
            String assignmentMessage = String.format(
                "Dear Teacher,\n\n" +
                "This is a reminder about assignment submissions:\n\n" +
                "Assignment: Algebra Practice Problems\n" +
                "Class: Form 2A\n" +
                "Due Date: %s\n" +
                "Students Submitted: 15/30\n" +
                "Students Pending: 15\n\n" +
                "Please follow up with students who haven't submitted their assignments.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(TEACHER_EMAIL, assignmentSubject, assignmentMessage);
            log.info("✅ Assignment reminder sent to teacher");

            // 4. Staff Meeting Reminder
            String meetingSubject = "👥 Staff Meeting Reminder";
            String meetingMessage = String.format(
                "Dear Teacher,\n\n" +
                "This is a reminder about the upcoming staff meeting:\n\n" +
                "Meeting: Weekly Staff Meeting\n" +
                "Date: %s\n" +
                "Time: 3:00 PM\n" +
                "Venue: Staff Room\n" +
                "Agenda: Academic progress review, upcoming events\n\n" +
                "Please ensure your attendance.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(TEACHER_EMAIL, meetingSubject, meetingMessage);
            log.info("✅ Staff meeting reminder sent to teacher");

            return ResponseEntity.ok(ApiResponse.success("All teacher notifications sent successfully to " + TEACHER_EMAIL));
        } catch (Exception e) {
            log.error("Error sending teacher notifications: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send teacher notifications: " + e.getMessage()));
        }
    }

    /**
     * Simulate all student notifications
     */
    @PostMapping("/student-notifications")
    public ResponseEntity<ApiResponse<String>> simulateStudentNotifications() {
        try {
            log.info("🎯 Starting Student Notification Simulation for: {}", STUDENT_EMAIL);
            
            // 1. Exam Reminder
            String examSubject = "📝 Exam Reminder - Mathematics";
            String examMessage = String.format(
                "Dear Student,\n\n" +
                "This is a reminder about your upcoming exam:\n\n" +
                "Exam: Mid-Term Mathematics Exam\n" +
                "Subject: Mathematics\n" +
                "Date: %s\n" +
                "Time: 09:00 AM\n" +
                "Duration: 2 hours\n" +
                "Room: Room 15\n\n" +
                "Please ensure you are well prepared and arrive on time.\n\n" +
                "Good luck!\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(STUDENT_EMAIL, examSubject, examMessage);
            log.info("✅ Exam reminder sent to student");

            // 2. Assignment Notification
            String assignmentSubject = "📋 New Assignment - Algebra Practice";
            String assignmentMessage = String.format(
                "Dear Student,\n\n" +
                "You have been assigned a new assignment:\n\n" +
                "Title: Algebra Practice Problems\n" +
                "Subject: Mathematics\n" +
                "Description: Complete exercises 1-20 from Chapter 5\n" +
                "Due Date: %s\n" +
                "Submission: Online through the school portal\n\n" +
                "Please complete and submit on time.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
            emailService.sendSimpleEmail(STUDENT_EMAIL, assignmentSubject, assignmentMessage);
            log.info("✅ Assignment notification sent to student");

            // 3. Library Book Due Reminder
            String librarySubject = "📚 Library Book Due Reminder";
            String libraryMessage = String.format(
                "Dear Student,\n\n" +
                "This is a reminder about your library book:\n\n" +
                "Book: Advanced Mathematics by Dr. Smith\n" +
                "Due Date: %s\n" +
                "Days Overdue: 2 days\n" +
                "Fine: KES 50.00\n\n" +
                "Please return the book to the library as soon as possible to avoid additional fines.\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(STUDENT_EMAIL, librarySubject, libraryMessage);
            log.info("✅ Library reminder sent to student");

            // 4. School Event Notification
            String eventSubject = "🎉 School Event - Science Fair";
            String eventMessage = String.format(
                "Dear Student,\n\n" +
                "You are invited to participate in the upcoming school event:\n\n" +
                "Event: Annual Science Fair\n" +
                "Date: %s\n" +
                "Time: 9:00 AM - 4:00 PM\n" +
                "Venue: School Hall\n" +
                "Theme: Innovation in Technology\n\n" +
                "Registration is open until %s. Don't miss this opportunity to showcase your projects!\n\n" +
                "Best regards,\nSchool Management System",
                LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(STUDENT_EMAIL, eventSubject, eventMessage);
            log.info("✅ School event notification sent to student");

            return ResponseEntity.ok(ApiResponse.success("All student notifications sent successfully to " + STUDENT_EMAIL));
        } catch (Exception e) {
            log.error("Error sending student notifications: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send student notifications: " + e.getMessage()));
        }
    }

    /**
     * Simulate all admin notifications
     */
    @PostMapping("/admin-notifications")
    public ResponseEntity<ApiResponse<String>> simulateAdminNotifications() {
        try {
            log.info("🎯 Starting Admin Notification Simulation for: {}", ADMIN_EMAIL);
            
            // 1. System Alert - Low Disk Space
            String systemSubject = "⚠️ System Alert - Low Disk Space";
            String systemMessage = String.format(
                "Dear Administrator,\n\n" +
                "This is a system alert regarding server resources:\n\n" +
                "Alert: Low Disk Space Warning\n" +
                "Server: School Management System\n" +
                "Disk Usage: 85%%\n" +
                "Available Space: 15 GB\n" +
                "Threshold: 20 GB\n" +
                "Time: %s\n\n" +
                "Please consider cleaning up old files or expanding storage capacity.\n\n" +
                "Best regards,\nSystem Administrator",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
            emailService.sendSimpleEmail(ADMIN_EMAIL, systemSubject, systemMessage);
            log.info("✅ System alert sent to admin");

            // 2. Security Alert - Failed Login Attempts
            String securitySubject = "🔒 Security Alert - Multiple Failed Login Attempts";
            String securityMessage = String.format(
                "Dear Administrator,\n\n" +
                "This is a security alert regarding suspicious activity:\n\n" +
                "Alert: Multiple Failed Login Attempts\n" +
                "User: student123\n" +
                "Attempts: 5 failed attempts\n" +
                "IP Address: 192.168.1.100\n" +
                "Time: %s\n" +
                "Status: Account temporarily locked\n\n" +
                "Please review the security logs and take appropriate action.\n\n" +
                "Best regards,\nSystem Administrator",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
            emailService.sendSimpleEmail(ADMIN_EMAIL, securitySubject, securityMessage);
            log.info("✅ Security alert sent to admin");

            // 3. Backup Completion Notification
            String backupSubject = "💾 Backup Completion Notification";
            String backupMessage = String.format(
                "Dear Administrator,\n\n" +
                "This is to inform you about the completion of the daily backup:\n\n" +
                "Backup Type: Daily Database Backup\n" +
                "Status: ✅ Completed Successfully\n" +
                "Size: 2.5 GB\n" +
                "Duration: 15 minutes\n" +
                "Time: %s\n" +
                "Location: /backups/daily_%s.sql\n\n" +
                "The backup has been stored securely and is ready for restoration if needed.\n\n" +
                "Best regards,\nSystem Administrator",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );
            emailService.sendSimpleEmail(ADMIN_EMAIL, backupSubject, backupMessage);
            log.info("✅ Backup notification sent to admin");

            // 4. Monthly Report Summary
            String reportSubject = "📊 Monthly System Report - October 2025";
            String reportMessage = String.format(
                "Dear Administrator,\n\n" +
                "Here is your monthly system report for October 2025:\n\n" +
                "📈 Key Statistics:\n" +
                "• Total Users: 1,250\n" +
                "• Active Students: 800\n" +
                "• Active Teachers: 45\n" +
                "• Parents: 750\n" +
                "• Emails Sent: 2,450\n" +
                "• System Uptime: 99.8%%\n\n" +
                "📋 Recent Activities:\n" +
                "• 150 new student enrollments\n" +
                "• 25 teacher performance reviews\n" +
                "• 1,200 fee payments processed\n" +
                "• 3,500 attendance records updated\n\n" +
                "Best regards,\nSystem Administrator",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            emailService.sendSimpleEmail(ADMIN_EMAIL, reportSubject, reportMessage);
            log.info("✅ Monthly report sent to admin");

            return ResponseEntity.ok(ApiResponse.success("All admin notifications sent successfully to " + ADMIN_EMAIL));
        } catch (Exception e) {
            log.error("Error sending admin notifications: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send admin notifications: " + e.getMessage()));
        }
    }

    /**
     * Simulate all notifications for all user types
     */
    @PostMapping("/all-notifications")
    public ResponseEntity<ApiResponse<String>> simulateAllNotifications() {
        try {
            log.info("🎯 Starting Complete Notification Simulation for All Users");
            
            // Send notifications to all user types
            simulateParentNotifications();
            Thread.sleep(2000); // Small delay between batches
            
            simulateTeacherNotifications();
            Thread.sleep(2000);
            
            simulateStudentNotifications();
            Thread.sleep(2000);
            
            simulateAdminNotifications();
            
            return ResponseEntity.ok(ApiResponse.success(
                "All notifications sent successfully to all user types:\n" +
                "• Parent: " + PARENT_EMAIL + "\n" +
                "• Teacher: " + TEACHER_EMAIL + "\n" +
                "• Student: " + STUDENT_EMAIL + "\n" +
                "• Admin: " + ADMIN_EMAIL
            ));
        } catch (Exception e) {
            log.error("Error sending all notifications: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send all notifications: " + e.getMessage()));
        }
    }

    /**
     * Send welcome emails to all users
     */
    @PostMapping("/welcome-all")
    public ResponseEntity<ApiResponse<String>> sendWelcomeToAll() {
        try {
            log.info("🎯 Sending Welcome Emails to All Users");
            
            // Welcome Parent
            emailService.sendWelcomeEmail(PARENT_EMAIL, "Alexina's Parent", "PARENT");
            log.info("✅ Welcome email sent to parent");
            
            // Welcome Teacher
            emailService.sendWelcomeEmail(TEACHER_EMAIL, "Ms. Nyabigena", "TEACHER");
            log.info("✅ Welcome email sent to teacher");
            
            // Welcome Student
            emailService.sendWelcomeEmail(STUDENT_EMAIL, "Alexina Johnson", "STUDENT");
            log.info("✅ Welcome email sent to student");
            
            // Welcome Admin
            emailService.sendWelcomeEmail(ADMIN_EMAIL, "Mr. Boaz", "ADMIN");
            log.info("✅ Welcome email sent to admin");
            
            return ResponseEntity.ok(ApiResponse.success("Welcome emails sent to all users successfully"));
        } catch (Exception e) {
            log.error("Error sending welcome emails: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to send welcome emails: " + e.getMessage()));
        }
    }
}

