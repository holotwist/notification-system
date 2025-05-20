package com.topglobales.comportamientoptrn.patronescomportamiento.model;

/**
 * Observer Interface: Declares the update method called by the Subject.
 * Users (and perhaps other components like logging services) will implement this.
 */
interface EventListener {
    /**
     * Called by the EventManager (Subject) when a relevant event occurs.
     *
     * @param eventType The type of event that occurred (e.g., "profileUpdate", "securityAlert").
     * @param message   The raw message associated with the event.
     */
    void update(String eventType, String message);
}