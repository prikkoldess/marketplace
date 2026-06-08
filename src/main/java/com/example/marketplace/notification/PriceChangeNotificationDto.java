package com.example.marketplace.notification;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceChangeNotificationDto {
    private String userEmail;
    private String firstName;
    private String productTitle;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}
