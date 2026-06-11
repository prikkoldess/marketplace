package com.example.marketplace.auth;

import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    private String refreshToken;
}
