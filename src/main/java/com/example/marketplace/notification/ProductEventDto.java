package com.example.marketplace.notification;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEventDto {
    private UUID eventId;
    private Long productId;
    private String productTitle;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}
