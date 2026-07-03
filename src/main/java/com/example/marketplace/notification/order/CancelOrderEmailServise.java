package com.example.marketplace.notification.order;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.config.RabbitMQConfig;
import com.example.marketplace.notification.NotificationCheck;
import com.example.marketplace.notification.NotificationCheckRepository;
import com.example.marketplace.user.User;
import com.example.marketplace.user.UserRepository;

@Service
public class CancelOrderEmailServise {
    private final JavaMailSender javaMailSender;
    private final NotificationCheckRepository notificationCheckRepository;
    private final UserRepository userRepository;

    public CancelOrderEmailServise(JavaMailSender javaMailSender,
            NotificationCheckRepository notificationCheckRepository, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.notificationCheckRepository = notificationCheckRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.QUEUE_CANCEL_ORDER)
    public void cancelOrderNotification(CancelOrderNotificationDto dto) {
        String uniqueKey = dto.eventId().toString() + dto.orderId();

        if (notificationCheckRepository.existsByNotificationKey(uniqueKey)) {
            return;
        }

        User seller = userRepository.findUserByMerchantId(dto.merchantId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Seller not found"));

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(seller.getEmail());
        mailMessage.setSubject("Order Cancellation Notice");
        mailMessage.setText(String.format(
                "Hello %s,\n\n" +
                        "Please be advised that the buyer has cancelled order #%d.\n" +
                        "The items have been automatically restocked to your inventory.\n\n" +
                        "Please check your seller dashboard for further details.",
                seller.getFirstName(),
                dto.orderId()));

        javaMailSender.send(mailMessage);

        notificationCheckRepository.save(new NotificationCheck(uniqueKey, LocalDateTime.now()));
    }
}
