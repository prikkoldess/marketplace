package com.example.marketplace.notification;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.config.RabbitMQConfig;

@Service
public class NotificationService {
    private final JavaMailSender javaMailSender;
    private final NotificationCheckRepository notificationCheckRepository;

    public NotificationService(JavaMailSender javaMailSender, NotificationCheckRepository notificationCheckRepository) {
        this.javaMailSender = javaMailSender;
        this.notificationCheckRepository = notificationCheckRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PRICE_DROP)
    public void priceDropNotification(PriceChangeNotificationDto notificationDto) {
        String uniqueNotification = notificationDto.getEventId() + notificationDto.getUserEmail();

        if (notificationCheckRepository.existsByNotificationKey(uniqueNotification)) {
            return;
        }

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

        notificationCheckRepository.save(new NotificationCheck(uniqueNotification, LocalDateTime.now()));
    }
}
