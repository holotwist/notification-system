package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

/**
 * Handler interface for the Chain of Responsibility.
 * Declares a method for handling requests and a method for setting the next handler.
 */
public interface ValidationHandler {
    /**
     * Sets the next handler in the chain.
     * @param next The next handler.
     * @return The next handler, to allow for fluent chain building.
     */
    ValidationHandler setNext(ValidationHandler next);

    /**
     * Handles (validates) the notification context.
     * @param context The notification context to validate.
     * @return true if the context is valid according to this handler and subsequent handlers, false otherwise.
     */
    boolean validate(NotificationContext context);
}