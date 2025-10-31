package com.schoolmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpesaStkPushResponse {
    private String checkoutRequestId;
    private String merchantRequestId;
    private String customerMessage;
    private String responseCode;
    private String responseDescription;
    private Long invoiceId;
    private String invoiceNumber;
    private String amount;
    private String phoneNumber;
}

