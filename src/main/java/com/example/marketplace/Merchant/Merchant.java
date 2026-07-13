package com.example.marketplace.Merchant;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String inviteCode;

    public Merchant(String name, String inviteCode) {
        this.name = name;
        this.inviteCode = inviteCode;
    }

    public void regenerateInviteCode() {
        this.inviteCode = "SHOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
