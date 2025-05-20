package com.topglobales.comportamientoptrn.patronescomportamiento.model;

import com.topglobales.comportamientoptrn.patronescomportamiento.validator.NotificationContext;
import com.topglobales.comportamientoptrn.patronescomportamiento.validator.ValidationHandler;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.*;

public class EventManager {
    // Maps event types to lists of listeners interested in that event.
    private final Map<String, List<EventListener>> listeners = new HashMap<>();
    private final ValidationHandler validationChain; // Head of the validation chain

    /**
     * Constructs an EventManager with a validation chain.
     * @param validationChain The first handler in the validation chain. Can be null if no validation is desired.
     */
    public EventManager(ValidationHandler validationChain) {
        this.validationChain = validationChain;
    }

    /**
     * Subscribes a listener to a specific event type.
     *
     * @param eventType The event type to subscribe to.
     * @param listener  The listener to add.
     */
    public void subscribe(String eventType, EventListener listener) {
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");
        // Get the list for the event type, creating it if it doesn't exist.
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
        System.out.printf("EventManager: %s subscribed to [%s]\n", getListenerName(listener), eventType);
    }

    /**
     * Unsubscribes a listener from a specific event type.
     *
     * @param eventType The event type to unsubscribe from.
     * @param listener  The listener to remove.
     */
    public void unsubscribe(String eventType, EventListener listener) {
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            if (eventListeners.remove(listener)) {
                System.out.printf("EventManager: %s unsubscribed from [%s]\n", getListenerName(listener), eventType);
                // Remove the event type key if no listeners remain
                if (eventListeners.isEmpty()) {
                    listeners.remove(eventType);
                }
            }
        }
    }

    /**
     * Notifies all listeners subscribed to a specific event type,
     * after passing the message through a validation chain.
     *
     * @param eventType The type of event that occurred.
     * @param message   The raw message data associated with the event.
     * @param logTarget The TextArea for logging UI messages.
     */
    public void notify(String eventType, String message, TextArea logTarget) {
        Objects.requireNonNull(eventType, "eventType cannot be null");

        String initialLog = String.format("--- EventManager: Received event [%s] with message: \"%s\" ---",
                eventType, message);
        logToUI(initialLog + "\n", logTarget);


        // --- Chain of Responsibility: Validate the notification ---
        NotificationContext notificationContext = new NotificationContext(eventType, message, logTarget);
        if (validationChain != null) {
            logToUI("--- EventManager: Starting validation chain... ---\n", logTarget);
            boolean isValid = validationChain.validate(notificationContext);
            if (!isValid || !notificationContext.isValid()) { // Check both chain's return and context's flag
                logToUI(String.format("--- EventManager: Validation failed for event [%s]. Notification aborted. ---\nReasons:\n%s\n",
                                eventType, String.join("\n", notificationContext.getValidationMessages())),
                        logTarget);
                return; // Stop processing if validation fails
            }
            logToUI("--- EventManager: Validation chain completed successfully. ---\n", logTarget);
        } else {
            logToUI("--- EventManager: No validation chain configured. Proceeding directly. ---\n", logTarget);
        }
        // Use validated/original message.
        // A more complex system might allow validators to modify the message. (perhaps in the future add that)
        String messageToDispatch = notificationContext.getOriginalMessage();


        List<EventListener> eventListeners = listeners.get(eventType);
        String notifyLog = String.format("--- EventManager: Triggering event [%s] for %d listener(s) ---",
                eventType, eventListeners != null ? eventListeners.size() : 0);
        logToUI(notifyLog + "\n", logTarget);


        if (eventListeners != null && !eventListeners.isEmpty()) {
            // Iterate over a copy to prevent ConcurrentModificationException (A try to manual Optimistic Locking)
            for (EventListener listener : new ArrayList<>(eventListeners)) {
                // The listener's update method will now use the Command pattern
                listener.update(eventType, messageToDispatch);
            }
        } else {
            String noListenersLog = String.format("--- EventManager: No listeners for event [%s]. ---", eventType);
            logToUI(noListenersLog + "\n\n", logTarget);
        }
    }

    private void logToUI(String message, TextArea logTarget) {
        if (logTarget != null) {
            Platform.runLater(() -> logTarget.appendText(message));
        } else {
            System.out.print(message); // Fallback
        }
    }

    // Helper to get a printable name for the listener (assuming it's a User)
    private String getListenerName(EventListener listener) {
        if (listener instanceof User) {
            return ((User) listener).getName();
        }
        return listener.getClass().getSimpleName();
    }
}