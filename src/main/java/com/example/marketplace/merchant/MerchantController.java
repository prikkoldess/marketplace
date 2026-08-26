package com.example.marketplace.merchant;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.security.UserPrincipal;

@RestController
@RequestMapping("merchant")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/invite-code/regenerate")
    @PreAuthorize("hasRole('SELLER')")
    public String regenerateInviteCode(@AuthenticationPrincipal UserPrincipal user) {
        Long userId = user.getId();
        return merchantService.regenerateInviteCode(userId);
    }
}
