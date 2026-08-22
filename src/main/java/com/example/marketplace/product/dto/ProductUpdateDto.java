package com.example.marketplace.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record ProductUpdateDto(
        @Min(value = 0, message = "The quantity cannot be negative.") @Schema(example = "10") Integer quantity,
        @Positive(message = "Price must be greater than zero") BigDecimal price)

{
}
