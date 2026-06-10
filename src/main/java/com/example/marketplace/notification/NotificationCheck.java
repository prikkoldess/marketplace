package com.example.marketplace.notification;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String notificationKey;

    private LocalDateTime sentAt;

    public NotificationCheck(String notificationKey, LocalDateTime sentAt) {
        this.notificationKey = notificationKey;
        this.sentAt = sentAt;
    }
}
