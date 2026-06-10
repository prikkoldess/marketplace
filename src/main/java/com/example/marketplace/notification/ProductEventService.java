package com.example.marketplace.notification;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.config.RabbitMQConfig;
import com.example.marketplace.product.Product;
import com.example.marketplace.product.ProductRepository;
import com.example.marketplace.user.User;
import com.example.marketplace.wishlist.Wishlist;
import com.example.marketplace.wishlist.WishlistRepository;

@Service
public class ProductEventService {

    private final WishlistRepository repository;
    private final RabbitTemplate rabbitTemplate;
    public final ProductRepository productRepository;

    public ProductEventService(WishlistRepository repository, RabbitTemplate rabbitTemplate,
            ProductRepository productRepository) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRODUCT_EVENT)
    public void handlePriceDropEvent(ProductEventDto eventDto) {
        Product currentProduct = productRepository.findById(eventDto.getProductId()).orElse(null);

        if (currentProduct == null || currentProduct.getPrice().compareTo(eventDto.getNewPrice()) != 0) {
            return;
        }

        List<Wishlist> users = repository.findByProductId(eventDto.getProductId());

        for (Wishlist user : users) {
            User buyer = user.getBuyer();

            PriceChangeNotificationDto notificationDto = new PriceChangeNotificationDto(eventDto.getEventId(),
                    buyer.getEmail(),
                    buyer.getFirstName(),
                    eventDto.getProductTitle(),
                    eventDto.getOldPrice(),
                    eventDto.getNewPrice());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_PRICE_DROP,
                    RabbitMQConfig.ROUTING_KEY_PRICE_DROP,
                    notificationDto);
        }

    }
}
