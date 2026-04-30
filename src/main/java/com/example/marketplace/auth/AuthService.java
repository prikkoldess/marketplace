package com.example.marketplace.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.security.JwtService;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BasketRepository basketRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService, BasketRepository basketRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.basketRepository = basketRepository;
    }

    @Transactional
    public UserDto registerSeller(CreateUserDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User seller = User.registerSeller(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedUser = userRepository.save(seller);

        return mapToDto(savedUser);
    }

    @Transactional
    public UserDto registerBuyer(CreateUserDto dto) {
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
        return dto;
    }

}
