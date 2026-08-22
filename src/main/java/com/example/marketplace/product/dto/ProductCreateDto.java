package com.example.marketplace.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateDto(
                @NotBlank(message = "Title cannot be empty") String title,

                @NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer quantity,

                @NotNull(message = "Price is required") @Positive(message = "Price must be greater than zero") BigDecimal price) {

}
