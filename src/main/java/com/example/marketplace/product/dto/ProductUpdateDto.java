package com.example.marketplace.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductUpdateDto(
                @Schema(example = "10") Integer quantity,
                BigDecimal price)

{
}
