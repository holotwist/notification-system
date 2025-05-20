package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Subclass (Template Method): Implements formatting for Admin users.
 */
public class AdminUser extends User {
    public AdminUser(String name, String email, String phoneNumber, NotificationStrategy initialStrategy) {
        super(name, email, phoneNumber, initialStrategy);
    }

    @Override
    protected String getHeader() {
        return String.format("=== ADMIN ALERT (%s) ===", getName());
    }

    @Override
    protected String formatBody(String rawMessage) {
        // For admin log
        return "[ADMIN] Details: " + rawMessage.toUpperCase();
    }


    @Override
    protected String getFooter() {
        return "=== Action Required ===";
    }
}