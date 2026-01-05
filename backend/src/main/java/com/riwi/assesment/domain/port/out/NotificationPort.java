package com.riwi.assesment.domain.port.out;

/**
 * Output port for notification operations.
 * This interface defines the contract for sending notifications.
 * Implementations can use different strategies (email, SMS, push, etc.)
 * following the Strategy pattern.
 */
public interface NotificationPort {

    /**
     * Sends a notification with the given message.
     * @param message the notification message to send
     */
    void notify(String message);
}
