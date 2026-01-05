package com.riwi.assesment.infrastructure.adapter.out.notification;

import com.riwi.assesment.domain.port.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter implementing NotificationPort.
 * This implementation uses logging as a simple notification mechanism.
 * In a production environment, this could be replaced with email, SMS, push notifications, etc.
 * Following the Strategy pattern, different notification strategies can be injected.
 */
@Component
public class ConsoleNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleNotificationAdapter.class);

    @Override
    public void notify(String message) {
        logger.info("[NOTIFICATION] {}", message);
    }
}
