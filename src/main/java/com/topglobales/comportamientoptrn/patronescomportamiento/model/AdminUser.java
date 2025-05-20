package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete subclass of User that defines how notifications are formatted for Admin users.
 * Follows the Template Method pattern.
 */
public class AdminUser extends User {

    public AdminUser(String name, String email, String phoneNumber, NotificationStrategy initialStrategy) {
        super(name, email, phoneNumber, initialStrategy);
    }

    @Override
    protected String getHeader() {
        // Custom header for admin alerts
        return String.format("=== ADMIN ALERT (%s) ===", getName());
    }

    @Override
    protected String formatBody(String rawMessage) {
        // Emphasize message content for admin
        return "[ADMIN] Details: " + rawMessage.toUpperCase();
    }

    @Override
    protected String getFooter() {
        // Footer indicating urgency
        return "=== Action Required ===";
    }
}
