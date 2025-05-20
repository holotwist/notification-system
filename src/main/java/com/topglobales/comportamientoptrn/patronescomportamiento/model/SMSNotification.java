package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Strategy: Implements sending notifications via SMS.
 */
public class SMSNotification implements NotificationStrategy {
    @Override
    public String send(User user, String message) {
        String output = String.format("--- SMS Sent ---\nTo: %s (%s)\n%s\n----------------",
                user.getName(), user.getPhoneNumber(), message);
        return output; // Return the string
    }
    @Override public String toString() { return "SMS Channel"; }
}