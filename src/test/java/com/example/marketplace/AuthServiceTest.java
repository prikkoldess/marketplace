package com.example.marketplace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.marketplace.auth.AuthService;
import com.example.marketplace.basket.Basket;
import com.example.marketplace.basket.BasketRepository;
import com.example.marketplace.user.Role;
import com.example.marketplace.user.Status;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.user.dto.CreateUserDto;
import com.example.marketplace.user.dto.UserDto;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    BasketRepository basketRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthService authService;

    @Captor
    ArgumentCaptor<User> usercaptor;

    @Test
    void registerSeller() {
        CreateUserDto seller = new CreateUserDto();
        seller.setEmail("seller@gmail.com");
        seller.setFirstName("Dan");
        seller.setLastName("Jones");
        seller.setPassword("sellerpass");

        when(passwordEncoder.encode("sellerpass")).thenReturn("encoded_pass");
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = authService.registerSeller(seller);

        assertEquals("Dan", result.getFirstName());
        assertEquals("Jones", result.getLastName());
        assertEquals("seller@gmail.com", result.getEmail());
        verify(repository).save(usercaptor.capture());

        User savedSeller = usercaptor.getValue();

        assertEquals(Role.SELLER, savedSeller.getRole());
        assertEquals(Status.ACTIVE, savedSeller.getStatus());
        assertEquals("encoded_pass", savedSeller.getPassword());
        verify(passwordEncoder).encode("sellerpass");
    }

    @Test
    void registerBuyer() {
        CreateUserDto buyer = new CreateUserDto();
        buyer.setEmail("buyer@gmail.com");
        buyer.setFirstName("Ben");
        buyer.setLastName("Jones");
        buyer.setPassword("buyerpass");

        when(passwordEncoder.encode("buyerpass")).thenReturn("encoded_pass");
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(basketRepository.save(any(Basket.class))).thenAnswer(i -> i.getArgument(0));
        UserDto result = authService.registerBuyer(buyer);

        assertEquals("Ben", result.getFirstName());
        assertEquals("Jones", result.getLastName());
        assertEquals("buyer@gmail.com", result.getEmail());
        verify(basketRepository, times(1)).save(any(Basket.class));
        verify(repository).save(usercaptor.capture());

        User savedBuyer = usercaptor.getValue();

        assertEquals(Role.BUYER, savedBuyer.getRole());
        assertEquals(Status.ACTIVE, savedBuyer.getStatus());
        assertEquals("encoded_pass", savedBuyer.getPassword());

        verify(passwordEncoder).encode("buyerpass");
    }
}
