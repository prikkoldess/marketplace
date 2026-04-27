package com.example.marketplace.product.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {
    private String title;
    private Integer quantity;
    private BigDecimal price;

}
