package com.example.marketplace.user;

import java.util.ArrayList;
import java.util.List;

import com.example.marketplace.product.Product;
import com.example.marketplace.wishlist.Wishlist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
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

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlistItems = new ArrayList<>();

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

    public void addToWishlist(Product product) {
        boolean exist = this.wishlistItems.stream().anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (exist) {
            throw new IllegalStateException("The product is already in your favorites");
        }

        Wishlist newItem = new Wishlist(this, product);
        this.wishlistItems.add(newItem);
    }

    public void removeFromWishlist(Product product) {
        boolean remove = this.wishlistItems.removeIf(items -> items.getProduct().getId().equals(product.getId()));

        if (!remove) {
            throw new IllegalArgumentException("The product was not found in your wishlist");
        }
    }
}
