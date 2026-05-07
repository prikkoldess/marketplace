package com.example.marketplace.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.marketplace.config.RabbitMQConfig;

@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRICE_DROP)
    public void priceDropNotification(PriceChangeNotificationDto notificationDto) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(notificationDto.getUserEmail());
        mailMessage.setSubject("Changing the price of an product on your Wishlist!");
        mailMessage.setText(String.format(
                "Hello, %s!\n\nThe price of item '%s' from your wishlist has changed!\nOld price: %s\nNew price: %s\n\nHurry to buy!",
                notificationDto.getFirstName(),
                notificationDto.getProductTitle(),
                notificationDto.getOldPrice(),
                notificationDto.getNewPrice()));

        javaMailSender.send(mailMessage);

    }
}
