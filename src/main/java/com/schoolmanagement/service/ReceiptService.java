package com.schoolmanagement.service;

import com.schoolmanagement.entity.Payment;
import com.schoolmanagement.entity.FeeInvoice;
import com.schoolmanagement.entity.StudentEnrollment;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final PaymentRepository paymentRepository;

    public Resource generatePaymentReceipt(Long paymentId) {
        try {
            log.info("Generating payment receipt for payment: {}", paymentId);

            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                throw new RuntimeException("Payment not found");
            }

            Payment payment = paymentOpt.get();
            FeeInvoice invoice = payment.getInvoice();
            StudentEnrollment enrollment = payment.getEnrollment();
            User student = enrollment.getStudent();

            // Create receipt data
            Map<String, Object> receiptData = new HashMap<>();
            receiptData.put("receiptNumber", "RCP-" + payment.getId());
            receiptData.put("paymentDate", payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            receiptData.put("studentName", student.getFirstName() + " " + student.getLastName());
            receiptData.put("studentId", student.getUsername());
            receiptData.put("class", enrollment.getClassEntity().getName());
            receiptData.put("academicYear", "2024"); // Simplified for now
            receiptData.put("amount", payment.getAmount());
            receiptData.put("paymentMethod", payment.getPaymentMethod());
            receiptData.put("transactionId", payment.getTransactionId());
            receiptData.put("invoiceNumber", invoice.getInvoiceNumber());
            receiptData.put("schoolName", "Kenya School Management System");
            receiptData.put("schoolAddress", "Nairobi, Kenya");
            receiptData.put("phone", "+254 700 000 000");
            receiptData.put("email", "info@schoolmanagement.co.ke");

            // Generate PDF content
            String pdfContent = generateReceiptHTML(receiptData);
            byte[] pdfBytes = convertHTMLToPDF(pdfContent);

            return new ByteArrayResource(pdfBytes);

        } catch (Exception e) {
            log.error("Error generating payment receipt: {}", e.getMessage());
            throw new RuntimeException("Failed to generate receipt: " + e.getMessage());
        }
    }

    private String generateReceiptHTML(Map<String, Object> data) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Payment Receipt</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px; }
                    .school-name { font-size: 24px; font-weight: bold; color: #333; }
                    .school-details { font-size: 12px; color: #666; margin-top: 5px; }
                    .receipt-title { font-size: 20px; font-weight: bold; text-align: center; margin: 20px 0; }
                    .receipt-details { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    .receipt-details td { padding: 8px; border: 1px solid #ddd; }
                    .receipt-details .label { background-color: #f5f5f5; font-weight: bold; width: 30%%; }
                    .amount-section { background-color: #f9f9f9; padding: 15px; margin: 20px 0; border: 1px solid #ddd; }
                    .total-amount { font-size: 18px; font-weight: bold; color: #2c5aa0; }
                    .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #666; }
                    .signature { margin-top: 40px; }
                    .signature-line { border-top: 1px solid #333; width: 200px; margin: 0 auto; }
                </style>
            </head>
            <body>
                <div class="header">
                    <div class="school-name">%s</div>
                    <div class="school-details">
                        %s<br>
                        Tel: %s | Email: %s
                    </div>
                </div>
                
                <div class="receipt-title">PAYMENT RECEIPT</div>
                
                <table class="receipt-details">
                    <tr>
                        <td class="label">Receipt Number:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Payment Date:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Student Name:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Student ID:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Class:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Academic Year:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Invoice Number:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Payment Method:</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td class="label">Transaction ID:</td>
                        <td>%s</td>
                    </tr>
                </table>
                
                <div class="amount-section">
                    <div style="text-align: center;">
                        <div>Amount Paid</div>
                        <div class="total-amount">KES %s</div>
                    </div>
                </div>
                
                <div class="signature">
                    <div style="text-align: center; margin-bottom: 10px;">
                        <div>Authorized Signature</div>
                        <div class="signature-line"></div>
                    </div>
                </div>
                
                <div class="footer">
                    <p>This is a computer-generated receipt. No signature required.</p>
                    <p>Generated on: %s</p>
                </div>
            </body>
            </html>
            """,
            data.get("schoolName"),
            data.get("schoolAddress"),
            data.get("phone"),
            data.get("email"),
            data.get("receiptNumber"),
            data.get("paymentDate"),
            data.get("studentName"),
            data.get("studentId"),
            data.get("class"),
            data.get("academicYear"),
            data.get("invoiceNumber"),
            data.get("paymentMethod"),
            data.get("transactionId"),
            data.get("amount"),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    private byte[] convertHTMLToPDF(String htmlContent) {
        try {
            // This is a simplified implementation
            // In production, you would use a library like iText, Flying Saucer, or wkhtmltopdf
            // For now, we'll return the HTML content as bytes
            return htmlContent.getBytes();
        } catch (Exception e) {
            log.error("Error converting HTML to PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to convert HTML to PDF", e);
        }
    }

    public Resource generateFeeStatement(Long studentId, Long academicYearId) {
        try {
            log.info("Generating fee statement for student: {}, academic year: {}", studentId, academicYearId);

            // This would generate a comprehensive fee statement
            // For now, return a placeholder
            String statementContent = "Fee Statement - Implementation pending";
            return new ByteArrayResource(statementContent.getBytes());

        } catch (Exception e) {
            log.error("Error generating fee statement: {}", e.getMessage());
            throw new RuntimeException("Failed to generate fee statement: " + e.getMessage());
        }
    }
}
