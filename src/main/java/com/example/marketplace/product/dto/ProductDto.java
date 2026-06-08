package com.example.marketplace.product.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String title,
        Integer quantity,
        BigDecimal price) {
}
