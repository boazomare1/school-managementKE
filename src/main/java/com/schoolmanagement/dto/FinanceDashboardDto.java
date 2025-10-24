package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDashboardDto {

    // Summary Statistics
    private Long totalFeeStructures;
    private Long totalInvoices;
    private Long pendingInvoices;
    private Long paidInvoices;
    private Long overdueInvoices;
    private Long totalPayments;
    private Long successfulPayments;
    private Long failedPayments;

    // Financial Totals
    private BigDecimal totalFeeAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalBalanceAmount;
    private BigDecimal totalRefundAmount;
    private BigDecimal monthlyRevenue;
    private BigDecimal quarterlyRevenue;
    private BigDecimal annualRevenue;

    // Payment Method Breakdown
    private Map<String, BigDecimal> paymentMethodTotals;
    private Map<String, Long> paymentMethodCounts;

    // Fee Type Breakdown
    private Map<String, BigDecimal> feeTypeTotals;
    private Map<String, Long> feeTypeCounts;

    // Class-wise Statistics
    private List<ClassFinanceSummary> classSummaries;

    // Recent Activity
    private List<RecentPayment> recentPayments;
    private List<OverdueInvoice> overdueInvoicesList;

    // Charts Data
    private List<MonthlyRevenue> monthlyRevenueData;
    private List<PaymentTrend> paymentTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassFinanceSummary {
        private Long classId;
        private String className;
        private Long totalStudents;
        private Long paidStudents;
        private Long pendingStudents;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private BigDecimal collectionRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPayment {
        private Long paymentId;
        private String paymentReference;
        private BigDecimal amount;
        private String paymentMethod;
        private String studentName;
        private String className;
        private LocalDateTime paymentDate;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverdueInvoice {
        private Long invoiceId;
        private String invoiceNumber;
        private String studentName;
        private String className;
        private BigDecimal amount;
        private BigDecimal balanceAmount;
        private LocalDateTime dueDate;
        private Long daysOverdue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal amount;
        private Long paymentCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTrend {
        private String date;
        private BigDecimal amount;
        private Long count;
    }
}
