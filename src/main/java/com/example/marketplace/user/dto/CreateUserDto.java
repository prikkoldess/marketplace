package com.example.marketplace.user.dto;

import lombok.Data;

@Data
public class CreateUserDto {
    private String password;
    private String email;
    private String firstName;
    private String lastName;

}
