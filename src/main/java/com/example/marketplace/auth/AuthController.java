package com.example.marketplace.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.admin.CreateAdminDto;
import com.example.marketplace.user.dto.CreateBuyerDto;
import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @PostMapping("/register/buyer")
    public UserDto registerBuyer(@Valid @RequestBody CreateBuyerDto request) {
        return authService.registerBuyer(request);
    }

    @PostMapping("/register/seller")
    public UserDto registerSeller(@Valid @RequestBody CreateUserDto request) {
        return authService.registerSeller(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto request, HttpServletResponse response) {
        AuthResponseDto tokens = authService.login(request);
        Cookie refreshTokenCookie = new Cookie("refresh_token", tokens.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/auth/refresh");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new AuthResponseDto(tokens.getAccessToken(), null));
    }

    @PostMapping("/register/admin")
    public UserDto registerAdmin(@Valid @RequestBody CreateAdminDto request) {
        return authService.registerAdmin(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshTokens(
            @Parameter(hidden = true) @CookieValue(name = "refresh_token", required = true) String refreshToken,
            HttpServletResponse response) {
        AuthResponseDto newTokens = authService.refreshTokens(refreshToken);
        Cookie newRefreshTokenCookie = new Cookie("refresh_token", newTokens.getRefreshToken());
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(false);
        newRefreshTokenCookie.setPath("/auth/refresh");
        newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(newRefreshTokenCookie);

        return ResponseEntity.ok(new AuthResponseDto(newTokens.getAccessToken(), null));
    }

}
