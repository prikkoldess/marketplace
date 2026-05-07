package com.example.marketplace.product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.config.RabbitMQConfig;
import com.example.marketplace.notification.PriceChangeNotificationDto;
import com.example.marketplace.product.dto.ProductCreateDto;
import com.example.marketplace.product.dto.ProductDto;
import com.example.marketplace.product.dto.ProductUpdateDto;
import com.example.marketplace.user.Role;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;
import com.example.marketplace.wishlist.Wishlist;
import com.example.marketplace.wishlist.WishlistRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final RabbitTemplate rabbitTemplate;

    public ProductService(ProductRepository productRepository, UserRepository userRepository,
            WishlistRepository wishlistRepository, RabbitTemplate rabbitTemplate) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
        this.rabbitTemplate = rabbitTemplate;
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

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        productRepository.delete(product);
    }

    public void hideProduct(Long productId, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(productId, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.hideProduct();
    }

    @Transactional
    public void hideProductByAdmin(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.hideProduct();
    }

    public ProductDto updateProduct(Long productId, Long sellerId, ProductUpdateDto dto) {
        Product product = productRepository.findByIdAndSellerId(productId, sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BigDecimal oldPrice = product.getPrice();

        product.updateProduct(dto.getQuantity(), dto.getPrice());
        Product savedProduct = productRepository.save(product);

        if (dto.getPrice() != null && dto.getPrice().compareTo(oldPrice) < 0) {
            List<Wishlist> wishlists = wishlistRepository.findByProductId(productId);
            for (Wishlist wishlist : wishlists) {
                PriceChangeNotificationDto notificationDto = new PriceChangeNotificationDto(
                        wishlist.getBuyer().getEmail(),
                        wishlist.getBuyer().getFirstName(),
                        product.getTitle(),
                        oldPrice,
                        dto.getPrice());

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_PRICE_DROP,
                        RabbitMQConfig.ROUTING_KEY_PRICE_DROP,
                        notificationDto);
            }
        }

        return mapToDto(savedProduct);
    }

    @Transactional
    public void unlockProduct(Long id, Long sellerId) {
        Product product = productRepository.findByIdAndSellerId(id, sellerId)
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

        return productRepository.findBySellerId(sellerId)
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
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
