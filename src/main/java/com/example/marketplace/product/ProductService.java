package com.example.marketplace.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.config.RabbitMQConfig;

import com.example.marketplace.notification.ProductEventDto;
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
    private final RabbitTemplate rabbitTemplate;

    public ProductService(ProductRepository productRepository, UserRepository userRepository,
            RabbitTemplate rabbitTemplate) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UUID getMerchantId(Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        if (seller.getMerchant() == null) {
            throw new IllegalStateException("Seller does not have an associated merchant account");
        }

        return seller.getMerchant().getId();
    }

    public ProductDto createProduct(ProductCreateDto dto, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new IllegalStateException("Only sellers can create products");
        }
        Product product = Product.createProduct(dto.title(), dto.price(), dto.quantity(), seller.getMerchant());
        Product savedProduct = productRepository.save(product);

        return mapToDto(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        UUID merchantId = getMerchantId(sellerId);
        Product product = productRepository.findByIdAndMerchantId(id, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    @Transactional
    public void hideProduct(Long productId, Long sellerId) {
        UUID merchantId = getMerchantId(sellerId);
        Product product = productRepository.findByIdAndMerchantId(productId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.hideProduct();
    }

    @Transactional
    public void hideProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.hideProduct();
    }

    @Transactional
    public ProductDto updateProduct(Long productId, Long sellerId, ProductUpdateDto dto) {
        UUID merchantId = getMerchantId(sellerId);
        Product product = productRepository.findByIdAndMerchantId(productId, merchantId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal oldPrice = product.getPrice();

        product.updateProduct(dto.quantity(), dto.price());
        Product savedProduct = productRepository.save(product);

        if (dto.price() != null && dto.price().compareTo(oldPrice) < 0) {
            ProductEventDto eventDto = new ProductEventDto(productId, product.getTitle(), oldPrice, dto.price());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_PRODUCT_EVENT_DELAY,
                    RabbitMQConfig.ROUTING_KEY_PRODUCT_EVENT_DELAY,
                    eventDto);

        }

        return mapToDto(savedProduct);
    }

    @Transactional
    public void unlockProduct(Long id, Long sellerId) {
        UUID mercantId = getMerchantId(sellerId);
        Product product = productRepository.findByIdAndMerchantId(id, mercantId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.unlockProduct();
    }

    @Transactional
    public void unlockProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.unlockProduct();
    }

    public List<ProductDto> getAllSellerProduct(Long sellerId) {
        UUID merchantId = getMerchantId(sellerId);

        return productRepository.findByMerchantId(merchantId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProductDto mapToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getTitle(),
                product.getQuantity(),
                product.getPrice());
    }
}
