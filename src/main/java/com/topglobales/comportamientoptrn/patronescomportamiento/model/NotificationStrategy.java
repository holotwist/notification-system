package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Strategy Interface: Declares the method for sending notifications.
 * Concrete strategies implement this interface to provide specific channel behavior.
 */
public interface NotificationStrategy {
    /**
     * Sends a notification message to a user.
     *
     * @param user    The recipient user.
     * @param message The message content.
     * @return        The user's message.
     */
    String send(User user, String message);
}