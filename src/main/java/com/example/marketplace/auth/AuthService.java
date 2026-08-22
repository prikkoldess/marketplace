package com.example.marketplace.auth;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.admin.CreateAdminDto;
import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.merchant.Merchant;
import com.example.marketplace.merchant.MerchantRepository;
import com.example.marketplace.security.JwtService;
import com.example.marketplace.security.RefreshTokenService;
import com.example.marketplace.user.Status;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.user.dto.CreateBuyerDto;
import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BasketRepository basketRepository;
    private final MerchantRepository merchantRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService, BasketRepository basketRepository,
            MerchantRepository merchantRepository, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.basketRepository = basketRepository;
        this.merchantRepository = merchantRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public UserDto registerSeller(CreateUserDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());
        User seller = User.registerSeller(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);

        if (dto.getInviteCode() != null && !dto.getInviteCode().isBlank()) {
            Merchant existingMerchant = merchantRepository.findByInviteCode(dto.getInviteCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid invitation code!"));
            seller.setMerchant(existingMerchant);
        } else {
            if (dto.getMerchantName() == null || dto.getMerchantName().isBlank()) {
                throw new IllegalArgumentException("You must provide the company name or invitation code.");
            }

            String inviteCode = "SHOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Merchant merchant = new Merchant(dto.getMerchantName(), inviteCode);
            Merchant savedmerchant = merchantRepository.save(merchant);

            seller.setMerchant(savedmerchant);
        }

        User savedUser = userRepository.save(seller);
        return mapToDto(savedUser);
    }

    @Transactional
    public UserDto registerBuyer(CreateBuyerDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User buyer = User.registerBuyer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedBuyer = userRepository.save(buyer);

        Basket basket = new Basket(savedBuyer);
        basketRepository.save(basket);
        return mapToDto(savedBuyer);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return new AuthResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public UserDto registerAdmin(CreateAdminDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User admin = User.registerAdmin(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedAdmin = userRepository.save(admin);

        return mapToDto(savedAdmin);
    }

    public AuthResponseDto refreshTokens(String requestRefreshToken) {
        String email = refreshTokenService.getEmailBtyToken(requestRefreshToken);
        if (email == null) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() != Status.ACTIVE) {
            refreshTokenService.deleteRefreshToken(requestRefreshToken);
            throw new RuntimeException("User account is locked or disabled");
        }

        refreshTokenService.deleteRefreshToken(requestRefreshToken);

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(email);

        return new AuthResponseDto(newAccessToken, newRefreshToken);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        if (user.getMerchant() != null) {
            dto.setMerchantName(user.getMerchant().getName());
        }
        return dto;
    }

}
