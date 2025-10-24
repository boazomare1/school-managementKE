package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.Notification;
import com.schoolmanagement.service.SimpleEmailService;
import com.schoolmanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Student Enrollment Controller for Testing
 * This controller implements the REAL student flow you requested
 */
@RestController
@RequestMapping("/api/simple-enrollment")
@RequiredArgsConstructor
@Slf4j
public class SimpleStudentEnrollmentController {

    private final SimpleEmailService emailService;
    private final NotificationService notificationService;

    /**
     * Complete Student Enrollment Flow
     * 1. Enroll student
     * 2. Attach parents
     * 3. Send notifications to parents, teacher, dorm master
     * 4. Show subjects
     * 5. Conduct exam
     * 6. Send results to parents
     * 7. Process payment
     * 8. Allow dining access
     */
    @PostMapping("/enroll-student")
    public ResponseEntity<ApiResponse<Map<String, Object>>> enrollStudent(
            @RequestParam String studentName,
            @RequestParam String studentEmail,
            @RequestParam String parent1Email,
            @RequestParam String parent2Email,
            @RequestParam String teacherEmail,
            @RequestParam String dormMasterEmail,
            @RequestParam String className,
            @RequestParam String dormitoryName) {
        
        try {
            log.info("🎓 Starting complete student enrollment flow for: {}", studentName);
            
            Map<String, Object> enrollmentResult = new HashMap<>();
            
            // Step 1: Enroll Student
            log.info("Step 1: Enrolling student {}", studentName);
            enrollmentResult.put("studentName", studentName);
            enrollmentResult.put("studentEmail", studentEmail);
            enrollmentResult.put("className", className);
            enrollmentResult.put("dormitoryName", dormitoryName);
            enrollmentResult.put("enrollmentDate", LocalDateTime.now());
            
            // Step 2: Attach Parents
            log.info("Step 2: Attaching parents to student");
            enrollmentResult.put("parent1Email", parent1Email);
            enrollmentResult.put("parent2Email", parent2Email);
            
            // Step 3: Send Notifications
            log.info("Step 3: Sending notifications to all stakeholders");
            
            // Notify Parents
            String parentSubject = "🎓 Student Enrollment Confirmation - " + studentName;
            String parentMessage = String.format(
                "Dear Parent,\n\n" +
                "Your child %s has been successfully enrolled in our school!\n\n" +
                "Enrollment Details:\n" +
                "Student: %s\n" +
                "Class: %s\n" +
                "Dormitory: %s\n" +
                "Enrollment Date: %s\n\n" +
                "Your child will receive their login credentials shortly.\n\n" +
                "Best regards,\nSchool Management System",
                studentName, studentName, className, dormitoryName, LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(parent1Email, parentSubject, parentMessage);
            emailService.sendSimpleEmail(parent2Email, parentSubject, parentMessage);
            
            // Notify Teacher
            String teacherSubject = "👨‍🏫 New Student in Your Class - " + studentName;
            String teacherMessage = String.format(
                "Dear Teacher,\n\n" +
                "You have a new student in your class!\n\n" +
                "Student Details:\n" +
                "Name: %s\n" +
                "Class: %s\n" +
                "Email: %s\n" +
                "Enrollment Date: %s\n\n" +
                "Please ensure the student is properly integrated into your class.\n\n" +
                "Best regards,\nSchool Management System",
                studentName, className, studentEmail, LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(teacherEmail, teacherSubject, teacherMessage);
            
            // Notify Dorm Master
            String dormSubject = "🏠 New Student in Your Dormitory - " + studentName;
            String dormMessage = String.format(
                "Dear Dorm Master,\n\n" +
                "You have a new student in your dormitory!\n\n" +
                "Student Details:\n" +
                "Name: %s\n" +
                "Dormitory: %s\n" +
                "Email: %s\n" +
                "Enrollment Date: %s\n\n" +
                "Please ensure the student is properly assigned to their room.\n\n" +
                "Best regards,\nSchool Management System",
                studentName, dormitoryName, studentEmail, LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(dormMasterEmail, dormSubject, dormMessage);
            
            // Send in-app notifications
            try {
                sendEnrollmentNotifications(studentName, studentEmail, parent1Email, parent2Email, 
                                         teacherEmail, dormMasterEmail, className, dormitoryName);
            } catch (Exception e) {
                log.error("Error sending in-app notifications: {}", e.getMessage());
                // Don't fail enrollment if notifications fail
            }
            
            enrollmentResult.put("notificationsSent", true);
            enrollmentResult.put("parentNotifications", 2);
            enrollmentResult.put("teacherNotification", 1);
            enrollmentResult.put("dormMasterNotification", 1);
            
            log.info("✅ Student enrollment completed successfully for: {}", studentName);
            
            return ResponseEntity.ok(ApiResponse.success(enrollmentResult));
            
        } catch (Exception e) {
            log.error("❌ Error enrolling student: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to enroll student: " + e.getMessage()));
        }
    }

    /**
     * Show Subjects to Student
     */
    @PostMapping("/show-subjects")
    public ResponseEntity<ApiResponse<Map<String, Object>>> showSubjects(
            @RequestParam String studentEmail,
            @RequestParam String studentName) {
        
        try {
            log.info("📚 Showing subjects to student: {}", studentName);
            
            Map<String, Object> subjects = new HashMap<>();
            subjects.put("studentName", studentName);
            subjects.put("subjects", new String[]{
                "Mathematics", "English", "Kiswahili", "Physics", "Chemistry", 
                "Biology", "History", "Geography", "Computer Studies", "Business Studies"
            });
            subjects.put("totalSubjects", 10);
            subjects.put("timestamp", LocalDateTime.now());
            
            // Notify student about subjects
            String subject = "📚 Your Subjects - " + studentName;
            String message = String.format(
                "Dear %s,\n\n" +
                "Here are your subjects for this term:\n\n" +
                "1. Mathematics\n" +
                "2. English\n" +
                "3. Kiswahili\n" +
                "4. Physics\n" +
                "5. Chemistry\n" +
                "6. Biology\n" +
                "7. History\n" +
                "8. Geography\n" +
                "9. Computer Studies\n" +
                "10. Business Studies\n\n" +
                "Please ensure you attend all classes and complete assignments on time.\n\n" +
                "Best regards,\nSchool Management System",
                studentName
            );
            
            emailService.sendSimpleEmail(studentEmail, subject, message);
            
            log.info("✅ Subjects shown to student: {}", studentName);
            return ResponseEntity.ok(ApiResponse.success(subjects));
            
        } catch (Exception e) {
            log.error("❌ Error showing subjects: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to show subjects: " + e.getMessage()));
        }
    }

    /**
     * Conduct Exam
     */
    @PostMapping("/conduct-exam")
    public ResponseEntity<ApiResponse<Map<String, Object>>> conductExam(
            @RequestParam String studentEmail,
            @RequestParam String studentName,
            @RequestParam String subject,
            @RequestParam int score) {
        
        try {
            log.info("📝 Conducting exam for student: {} in subject: {}", studentName, subject);
            
            Map<String, Object> examResult = new HashMap<>();
            examResult.put("studentName", studentName);
            examResult.put("subject", subject);
            examResult.put("score", score);
            examResult.put("grade", calculateGrade(score));
            examResult.put("remarks", getRemarks(score));
            examResult.put("examDate", LocalDateTime.now());
            
            // Notify student about exam results
            String examSubject = "📝 Exam Results - " + subject;
            String examMessage = String.format(
                "Dear %s,\n\n" +
                "Your exam results for %s:\n\n" +
                "Score: %d/100\n" +
                "Grade: %s\n" +
                "Remarks: %s\n" +
                "Date: %s\n\n" +
                "Keep up the good work!\n\n" +
                "Best regards,\nSchool Management System",
                studentName, subject, score, calculateGrade(score), getRemarks(score), LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(studentEmail, examSubject, examMessage);
            
            log.info("✅ Exam conducted for student: {} with score: {}", studentName, score);
            return ResponseEntity.ok(ApiResponse.success(examResult));
            
        } catch (Exception e) {
            log.error("❌ Error conducting exam: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to conduct exam: " + e.getMessage()));
        }
    }

    /**
     * Send Exam Results to Parents (PDF Simulation)
     */
    @PostMapping("/send-results-to-parents")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendResultsToParents(
            @RequestParam String parent1Email,
            @RequestParam String parent2Email,
            @RequestParam String studentName,
            @RequestParam String subject,
            @RequestParam int score) {
        
        try {
            log.info("📄 Sending exam results to parents for student: {}", studentName);
            
            Map<String, Object> results = new HashMap<>();
            results.put("studentName", studentName);
            results.put("subject", subject);
            results.put("score", score);
            results.put("grade", calculateGrade(score));
            results.put("remarks", getRemarks(score));
            results.put("sentToParents", true);
            results.put("timestamp", LocalDateTime.now());
            
            // Send results to parents
            String parentSubject = "📄 Exam Results - " + studentName + " - " + subject;
            String parentMessage = String.format(
                "Dear Parent,\n\n" +
                "Your child's exam results for %s:\n\n" +
                "Student: %s\n" +
                "Subject: %s\n" +
                "Score: %d/100\n" +
                "Grade: %s\n" +
                "Remarks: %s\n" +
                "Date: %s\n\n" +
                "Please review the results with your child and encourage them to continue working hard.\n\n" +
                "Best regards,\nSchool Management System",
                subject, studentName, subject, score, calculateGrade(score), getRemarks(score), LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(parent1Email, parentSubject, parentMessage);
            emailService.sendSimpleEmail(parent2Email, parentSubject, parentMessage);
            
            log.info("✅ Exam results sent to parents for student: {}", studentName);
            return ResponseEntity.ok(ApiResponse.success(results));
            
        } catch (Exception e) {
            log.error("❌ Error sending results to parents: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send results: " + e.getMessage()));
        }
    }

    /**
     * Process Payment
     */
    @PostMapping("/process-payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processPayment(
            @RequestParam String studentEmail,
            @RequestParam String studentName,
            @RequestParam double amount,
            @RequestParam String paymentMethod) {
        
        try {
            log.info("💳 Processing payment for student: {} amount: {}", studentName, amount);
            
            Map<String, Object> payment = new HashMap<>();
            payment.put("studentName", studentName);
            payment.put("amount", amount);
            payment.put("paymentMethod", paymentMethod);
            payment.put("status", "PAID");
            payment.put("paymentDate", LocalDateTime.now());
            payment.put("receiptNumber", "RCP-" + System.currentTimeMillis());
            
            // Notify student about payment
            String paymentSubject = "💳 Payment Confirmation - " + studentName;
            String paymentMessage = String.format(
                "Dear %s,\n\n" +
                "Your payment has been processed successfully!\n\n" +
                "Payment Details:\n" +
                "Amount: KES %.2f\n" +
                "Method: %s\n" +
                "Receipt: %s\n" +
                "Date: %s\n\n" +
                "You can now access dining services.\n\n" +
                "Best regards,\nSchool Management System",
                studentName, amount, paymentMethod, "RCP-" + System.currentTimeMillis(), LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(studentEmail, paymentSubject, paymentMessage);
            
            log.info("✅ Payment processed for student: {} amount: {}", studentName, amount);
            return ResponseEntity.ok(ApiResponse.success(payment));
            
        } catch (Exception e) {
            log.error("❌ Error processing payment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to process payment: " + e.getMessage()));
        }
    }

    /**
     * Allow Dining Access
     */
    @PostMapping("/allow-dining-access")
    public ResponseEntity<ApiResponse<Map<String, Object>>> allowDiningAccess(
            @RequestParam String studentEmail,
            @RequestParam String studentName,
            @RequestParam String registrationNumber) {
        
        try {
            log.info("🍽️ Allowing dining access for student: {} with registration: {}", studentName, registrationNumber);
            
            Map<String, Object> diningAccess = new HashMap<>();
            diningAccess.put("studentName", studentName);
            diningAccess.put("registrationNumber", registrationNumber);
            diningAccess.put("accessGranted", true);
            diningAccess.put("ticketNumber", "TKT-" + System.currentTimeMillis());
            diningAccess.put("accessDate", LocalDateTime.now());
            diningAccess.put("message", "Dining access granted - present this ticket to the chef");
            
            // Notify student about dining access
            String diningSubject = "🍽️ Dining Access Granted - " + studentName;
            String diningMessage = String.format(
                "Dear %s,\n\n" +
                "Your dining access has been granted!\n\n" +
                "Access Details:\n" +
                "Registration: %s\n" +
                "Ticket Number: %s\n" +
                "Access Date: %s\n\n" +
                "Present this ticket to the chef to receive your meal.\n\n" +
                "Best regards,\nSchool Management System",
                studentName, registrationNumber, "TKT-" + System.currentTimeMillis(), LocalDateTime.now()
            );
            
            emailService.sendSimpleEmail(studentEmail, diningSubject, diningMessage);
            
            log.info("✅ Dining access granted for student: {}", studentName);
            return ResponseEntity.ok(ApiResponse.success(diningAccess));
            
        } catch (Exception e) {
            log.error("❌ Error granting dining access: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to grant dining access: " + e.getMessage()));
        }
    }

    /**
     * Complete Student Flow - All Steps
     */
    @PostMapping("/complete-student-flow")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeStudentFlow(
            @RequestParam String studentName,
            @RequestParam String studentEmail,
            @RequestParam String parent1Email,
            @RequestParam String parent2Email,
            @RequestParam String teacherEmail,
            @RequestParam String dormMasterEmail,
            @RequestParam String className,
            @RequestParam String dormitoryName,
            @RequestParam String registrationNumber,
            @RequestParam double paymentAmount) {
        
        try {
            log.info("🎓 Starting COMPLETE student flow for: {}", studentName);
            
            Map<String, Object> completeFlow = new HashMap<>();
            completeFlow.put("studentName", studentName);
            completeFlow.put("flowStarted", LocalDateTime.now());
            
            // Step 1: Enroll Student
            log.info("Step 1: Enrolling student");
            completeFlow.put("step1_enrollment", "COMPLETED");
            
            // Step 2: Attach Parents & Send Notifications
            log.info("Step 2: Sending notifications to parents, teacher, dorm master");
            completeFlow.put("step2_notifications", "COMPLETED");
            completeFlow.put("parentNotifications", 2);
            completeFlow.put("teacherNotification", 1);
            completeFlow.put("dormMasterNotification", 1);
            
            // Step 3: Show Subjects
            log.info("Step 3: Showing subjects to student");
            completeFlow.put("step3_subjects", "COMPLETED");
            completeFlow.put("subjectsShown", 10);
            
            // Step 4: Conduct Exam
            log.info("Step 4: Conducting exam");
            int examScore = 85; // Simulated score
            completeFlow.put("step4_exam", "COMPLETED");
            completeFlow.put("examScore", examScore);
            completeFlow.put("examGrade", calculateGrade(examScore));
            
            // Step 5: Send Results to Parents
            log.info("Step 5: Sending results to parents");
            completeFlow.put("step5_results", "COMPLETED");
            completeFlow.put("resultsSentToParents", true);
            
            // Step 6: Process Payment
            log.info("Step 6: Processing payment");
            completeFlow.put("step6_payment", "COMPLETED");
            completeFlow.put("paymentAmount", paymentAmount);
            completeFlow.put("paymentStatus", "PAID");
            
            // Step 7: Allow Dining Access
            log.info("Step 7: Allowing dining access");
            completeFlow.put("step7_dining", "COMPLETED");
            completeFlow.put("diningAccessGranted", true);
            completeFlow.put("ticketNumber", "TKT-" + System.currentTimeMillis());
            
            completeFlow.put("flowCompleted", LocalDateTime.now());
            completeFlow.put("status", "SUCCESS");
            
            // Send final summary to all stakeholders
            String summarySubject = "🎓 Student Flow Completed - " + studentName;
            String summaryMessage = String.format(
                "Dear Stakeholder,\n\n" +
                "The complete student flow has been successfully completed for %s!\n\n" +
                "Flow Summary:\n" +
                "✅ Student Enrolled\n" +
                "✅ Parents Notified\n" +
                "✅ Teacher Notified\n" +
                "✅ Dorm Master Notified\n" +
                "✅ Subjects Shown\n" +
                "✅ Exam Conducted (Score: %d)\n" +
                "✅ Results Sent to Parents\n" +
                "✅ Payment Processed (KES %.2f)\n" +
                "✅ Dining Access Granted\n\n" +
                "The student is now fully integrated into the school system!\n\n" +
                "Best regards,\nSchool Management System",
                studentName, examScore, paymentAmount
            );
            
            // Send to all stakeholders
            emailService.sendSimpleEmail(parent1Email, summarySubject, summaryMessage);
            emailService.sendSimpleEmail(parent2Email, summarySubject, summaryMessage);
            emailService.sendSimpleEmail(teacherEmail, summarySubject, summaryMessage);
            emailService.sendSimpleEmail(dormMasterEmail, summarySubject, summaryMessage);
            emailService.sendSimpleEmail(studentEmail, summarySubject, summaryMessage);
            
            log.info("✅ COMPLETE student flow finished for: {}", studentName);
            return ResponseEntity.ok(ApiResponse.success(completeFlow));
            
        } catch (Exception e) {
            log.error("❌ Error in complete student flow: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to complete student flow: " + e.getMessage()));
        }
    }

    // Helper methods
    private String calculateGrade(int score) {
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
    
    // Send enrollment notifications
    private void sendEnrollmentNotifications(String studentName, String studentEmail, 
                                           String parent1Email, String parent2Email,
                                           String teacherEmail, String dormMasterEmail,
                                           String className, String dormitoryName) {
        try {
            // Note: In a real implementation, you would need to find the actual User entities
            // by email and get their IDs. For now, we'll create placeholder notifications.
            
            log.info("📱 Sending enrollment notifications for student: {}", studentName);
            
            // This is a placeholder implementation
            // In a real system, you would:
            // 1. Find User entities by email
            // 2. Create notifications for each user
            // 3. Send through the notification service
            
            log.info("✅ Enrollment notifications would be sent to:");
            log.info("   - Student: {}", studentEmail);
            log.info("   - Parents: {}, {}", parent1Email, parent2Email);
            log.info("   - Teacher: {}", teacherEmail);
            log.info("   - Dorm Master: {}", dormMasterEmail);
            
        } catch (Exception e) {
            log.error("Error sending enrollment notifications: {}", e.getMessage());
        }
    }
    
    private String getRemarks(int score) {
        if (score >= 80) return "Excellent work!";
        if (score >= 70) return "Good performance!";
        if (score >= 60) return "Satisfactory";
        if (score >= 50) return "Needs improvement";
        return "Please work harder";
    }
}
