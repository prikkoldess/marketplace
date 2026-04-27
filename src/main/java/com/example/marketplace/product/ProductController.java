package com.example.marketplace.product;

import com.example.marketplace.security.UserPrincipal;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.product.dto.ProductUpdateDto;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/seller/create")
    public ProductDto createProduct(@RequestBody ProductCreateDto request,
            @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        return productService.createProduct(request, sellerId);
    }

    @PutMapping("/seller/hide")
    private void hideProduct(@RequestParam Long id, @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        productService.hideProduct(id, sellerId);
    }

    @PatchMapping("/seller/product/update")
    public ProductDto updateProduct(@RequestParam Long id, @AuthenticationPrincipal UserPrincipal seller,
            @RequestBody ProductUpdateDto dto) {
        Long sellerId = seller.getId();
        return productService.updateProduct(id, sellerId, dto);
    }

    @GetMapping("/users/all-products")
    public List<ProductDto> getAllProduct() {
        return productService.getAllProduct();
    }

    @PostMapping("/seller/product/unlock")
    public void unlockProduct(@RequestParam Long id, @AuthenticationPrincipal UserPrincipal seller) {
        Long sellerId = seller.getId();
        productService.unlockProduct(id, sellerId);
    }
}
