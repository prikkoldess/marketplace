package com.example.marketplace.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto registerSeller(CreateUserDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User seller = User.registerSeller(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedUser = userRepository.save(seller);

        return mapToDto(savedUser);
    }

    public UserDto registerBuyer(CreateUserDto dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User buyer = User.registerBuyer(dto.getFirstName(), dto.getLastName(), dto.getEmail(), passwordHash);
        User savedBuyer = userRepository.save(buyer);

        return mapToDto(savedBuyer);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
