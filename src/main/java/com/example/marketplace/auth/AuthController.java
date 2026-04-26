package com.example.marketplace.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/register/buyer")
    public UserDto registerBuyer(@RequestBody CreateUserDto request) {
        return authService.registerBuyer(request);
    }

    @PostMapping("/register/seller")
    public UserDto registerSeller(@RequestBody CreateUserDto request) {
        return authService.registerSeller(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginDto request) {
        return authService.login(request);
    }

}
