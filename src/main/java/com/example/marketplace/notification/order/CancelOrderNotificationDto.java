package com.example.marketplace.notification.order;

import java.util.UUID;

public record CancelOrderNotificationDto(
                UUID merchantId,
                UUID eventId,
                Long orderId) {

}
