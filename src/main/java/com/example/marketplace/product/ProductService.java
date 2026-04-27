package com.example.marketplace.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.product.dto.ProductUpdateDto;
import com.example.marketplace.user.Role;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ProductDto createProduct(ProductCreateDto dto, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new IllegalStateException("Only sellers can create products");
        }
        Product product = Product.createProduct(dto.getTitle(), dto.getPrice(), dto.getQuantity(), seller);
        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    public void hideProduct(Long id, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.hideProduct();
    }

    public ProductDto updateProduct(Long id, Long sellerId, ProductUpdateDto dto) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.updateProduct(dto.getQuantity(), dto.getPrice());
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    public void unlockProduct(Long id, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.save(product);
    }

    public List<ProductDto> getAllProduct() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setTitle(product.getTitle());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
