package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Concrete Subclass (Template Method): Implements formatting for Client users.
 */
public class ClientUser extends User {
    public ClientUser(String name, String email, String phoneNumber, NotificationStrategy initialStrategy) {
        super(name, email, phoneNumber, initialStrategy);
    }

    @Override
    protected String getHeader() {
        return String.format("Dear %s,", getName());
    }

    // Uses default formatBody from User class

    @Override
    protected String getFooter() {
        return "Thank you,\nFernely Industries";
    }
}