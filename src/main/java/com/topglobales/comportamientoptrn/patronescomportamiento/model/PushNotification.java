package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Strategy: Implements sending notifications via Push Notification.
 */
public class PushNotification implements NotificationStrategy {
    @Override
    public String send(User user, String message) {
        String output = String.format("--- Push Notification Sent ---\nDevice Target: %s\n%s\n--------------------------",
                user.getName(), message);
        return output; // Return the string
    }
    @Override public String toString() { return "Push Channel"; }
}