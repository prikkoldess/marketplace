package com.example.marketplace.notification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationCheckRepository extends JpaRepository<NotificationCheck, Long> {
    boolean existsByNotificationKey(String notificationKey);
}
