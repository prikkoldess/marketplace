package com.example.marketplace.product;

import com.example.marketplace.security.UserPrincipal;

import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.product.dto.ProductUpdateDto;

@RestController
@RequestMapping("/products")
@EnableMethodSecurity
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ProductDto createProduct(@Valid @RequestBody ProductCreateDto request,
            @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        return productService.createProduct(request, sellerId);
    }

    @PutMapping("/{id}/hide")
    @PreAuthorize("hasRole('SELLER')")
    public void hideProduct(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        productService.hideProduct(id, sellerId);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ProductDto updateProduct(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal seller,
            @Valid @RequestBody ProductUpdateDto dto) {
        Long sellerId = seller.getId();
        return productService.updateProduct(id, sellerId, dto);
    }

    @GetMapping
    public Page<ProductDto> getAllProducts(@ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SELLER')")
    public Page<ProductDto> getAllSellerProduct(@AuthenticationPrincipal UserPrincipal principal,
            @ParameterObject @PageableDefault(size = 20, page = 0) Pageable pageable) {
        Long sellerId = principal.getId();
        return productService.getAllSellerProduct(sellerId, pageable);
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasRole('SELLER')")
    public void unlockProduct(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        productService.unlockProduct(id, sellerId);
    }
}
