package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Strategy: Implements sending notifications via Email.
 */
public class EmailNotification implements NotificationStrategy {
    @Override
    public String send(User user, String message) {
        String output = String.format("--- Email Sent ---\nTo: %s (%s)\n%s\n------------------",
                user.getName(), user.getEmail(), message);
        return output; // Return the string
    }
    @Override public String toString() { return "Email Channel"; }
}