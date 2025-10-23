package com.schoolmanagement.controller;

import com.schoolmanagement.dto.ApiResponse;
import com.schoolmanagement.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Resource> generatePaymentReceipt(@PathVariable Long paymentId, Authentication authentication) {
        try {
            log.info("Generating payment receipt for payment: {}", paymentId);

            Resource receipt = receiptService.generatePaymentReceipt(paymentId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"payment_receipt_" + paymentId + ".pdf\"")
                    .body(receipt);

        } catch (Exception e) {
            log.error("Error generating payment receipt: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statement/{studentId}/{academicYearId}")
    public ResponseEntity<Resource> generateFeeStatement(
            @PathVariable Long studentId, 
            @PathVariable Long academicYearId, 
            Authentication authentication) {
        try {
            log.info("Generating fee statement for student: {}, academic year: {}", studentId, academicYearId);

            Resource statement = receiptService.generateFeeStatement(studentId, academicYearId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fee_statement_" + studentId + "_" + academicYearId + ".pdf\"")
                    .body(statement);

        } catch (Exception e) {
            log.error("Error generating fee statement: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/preview/{paymentId}")
    public ResponseEntity<ApiResponse<String>> previewReceipt(@PathVariable Long paymentId, Authentication authentication) {
        try {
            log.info("Previewing receipt for payment: {}", paymentId);

            // This would return a preview URL or HTML content
            String previewUrl = "/api/receipts/preview/" + paymentId;
            return ResponseEntity.ok(ApiResponse.success("Receipt preview generated", previewUrl));

        } catch (Exception e) {
            log.error("Error previewing receipt: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to preview receipt: " + e.getMessage()));
        }
    }
}

