package com.example.marketplace.Merchant;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByInviteCode(String inviteCode);
}
