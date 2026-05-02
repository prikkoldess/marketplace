package com.example.marketplace.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.annotations.security.SecurityScheme;

import org.springframework.context.annotation.Configuration;

@Configuration

@OpenAPIDefinition(

        info = @Info(title = "Marketplace API", version = "1.0", description = "Документация REST API для маркетплейса"),

        security = @SecurityRequirement(name = "bearerAuth")

)

@SecurityScheme(

        name = "bearerAuth",

        type = SecuritySchemeType.HTTP,

        scheme = "bearer",

        bearerFormat = "JWT",

        description = "Введите JWT токен, полученный при логине"

)

public class OpenApiConfig {

}
