package com.example.marketplace.product.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductUpdateDto {
    @Schema(example = "10")
    private Integer quantity;
    private BigDecimal price;

}
