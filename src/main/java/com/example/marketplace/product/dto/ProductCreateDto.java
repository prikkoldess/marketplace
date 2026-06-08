package com.example.marketplace.product.dto;

import java.math.BigDecimal;

public record ProductCreateDto(
        String title,
        Integer quantity,
        BigDecimal price) {

}
