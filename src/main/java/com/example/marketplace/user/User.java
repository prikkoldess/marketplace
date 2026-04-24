package com.example.marketplace.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;

    private String email;
    private String password;

    public User(String firstName, String lastName, Role role, Status status, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.email = email;
        this.password = password;
    }

    public static User registerSeller(String firstName, String lastName, String email, String password) {
        return new User(firstName,
                lastName,
                Role.SELLER,
                Status.ACTIVE,
                email,
                password);
    }

    public static User registerBuyer(String firstName, String lastName, String email, String password) {
        return new User(firstName,
                lastName,
                Role.BUYER,
                Status.ACTIVE,
                email,
                password);
    }

}
