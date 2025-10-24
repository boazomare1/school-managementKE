package com.schoolmanagement.dto;

import com.schoolmanagement.entity.NotificationDelivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeliveryDto {
    
    @NotNull(message = "Delivery channel is required")
    private NotificationDelivery.DeliveryChannel channel;
    
    private String deliveryMessage;
    private String externalId;
    private NotificationDelivery.DeliveryStatus status;
    private String errorMessage;
}
