package com.example.marketplace.notification.price;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceChangeNotificationDto {
    private UUID eventId;
    private String userEmail;
    private String firstName;
    private String productTitle;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}
