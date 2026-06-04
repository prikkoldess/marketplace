package com.example.marketplace.auth;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.Merchant.Merchant;
import com.example.marketplace.Merchant.MerchantRepository;
import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.security.JwtService;
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

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService, BasketRepository basketRepository,
            MerchantRepository merchantRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.basketRepository = basketRepository;
        this.merchantRepository = merchantRepository;
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

            Merchant merchant = new Merchant();
            merchant.setName(dto.getMerchantName());
            merchant.setInviteCode("SHOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            Merchant savedmerchant = merchantRepository.save(merchant);

            seller.setMerchant(merchant);
        }

        User savedUser = userRepository.save(seller);
        return mapToDto(savedUser);
    }

    @Transactional
    public UserDto registerAdmin(CreateUserDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User admin = User.registerAdmin(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedAdmin = userRepository.save(admin);

        return mapToDto(savedAdmin);
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

    public AuthResponseDto login(LoginDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);
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
