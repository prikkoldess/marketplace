package com.example.marketplace.Merchant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;

    public MerchantService(MerchantRepository merchantRepository, UserRepository userRepository) {
        this.merchantRepository = merchantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String regenerateInviteCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Merchant merchant = user.getMerchant();

        merchant.regenerateInviteCode();
        merchantRepository.save(merchant);

        return merchant.getInviteCode();
    }
}
