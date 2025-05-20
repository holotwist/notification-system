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
     * after passing the message through a validation chain for each relevant listener.
     *
     * @param eventType The type of event that occurred.
     * @param message   The raw message data associated with the event.
     * @param logTarget The TextArea for logging UI messages.
     */
    public void notify(String eventType, String message, TextArea logTarget) {
        Objects.requireNonNull(eventType, "eventType cannot be null");
        // Message content validation (e.g., not empty) will be handled by the chain.

        String initialLog = String.format("--- EventManager: Received event [%s] with raw message: \"%s\" ---",
                eventType, message);
        logToUI(initialLog + "\n", logTarget);

        List<EventListener> eventListeners = listeners.get(eventType);

        if (eventListeners == null || eventListeners.isEmpty()) {
            String noListenersLog = String.format("--- EventManager: No listeners for event [%s]. ---", eventType);
            logToUI(noListenersLog + "\n\n", logTarget);
            return;
        }

        String preNotifyLog = String.format("--- EventManager: Processing event [%s] for %d listener(s) ---",
                eventType, eventListeners.size());
        logToUI(preNotifyLog + "\n", logTarget);

        for (EventListener listener : new ArrayList<>(eventListeners)) { // Iterate over a copy
            User targetUser = (listener instanceof User) ? (User) listener : null;
            String listenerName = getListenerName(listener);

            // --- Chain of Responsibility: Validate the notification for this specific listener context ---
            // Context now includes the specific user if applicable, or null if listener is not a User.
            NotificationContext notificationContext = new NotificationContext(eventType, message, logTarget, targetUser);

            if (validationChain != null) {
                logToUI(String.format("--- EventManager: Starting validation chain for %s... ---\n", listenerName), logTarget);
                boolean chainPassed = validationChain.validate(notificationContext); // This updates context.isValid

                if (!chainPassed) { // If chain indicates failure (a validator returned false).
                    // The context's isValid flag should also be false.
                    logToUI(String.format("--- EventManager: Validation failed for %s regarding event [%s]. Notification aborted for this recipient. ---\nReasons:\n%s\n",
                                    listenerName, eventType, String.join("\n", notificationContext.getValidationMessages())),
                            logTarget);
                    continue; // Skip to the next listener
                }
                logToUI(String.format("--- EventManager: Validation chain completed successfully for %s. ---\n", listenerName), logTarget);
            } else {
                // No validation chain configured, proceed directly for this listener.
                logToUI(String.format("--- EventManager: No validation chain for %s. Proceeding directly. ---\n", listenerName), logTarget);
            }

            // Use validated/original message. A more complex system might allow validators to modify the message. (For now, I don't want to add that)
            String messageToDispatch = notificationContext.getOriginalMessage();

            logToUI(String.format("--- EventManager: Notifying %s for event [%s]... --- \n", listenerName, eventType), logTarget);
            listener.update(eventType, messageToDispatch);
        }
        logToUI(String.format("--- EventManager: Finished processing event [%s] for all applicable listeners. ---\n\n", eventType), logTarget);
    }

    private void logToUI(String message, TextArea logTarget) {
        if (logTarget != null) {
            Platform.runLater(() -> logTarget.appendText(message));
        } else {
            System.out.print(message); // Fallback
        }
    }

    // Helper to get a printable name for the listener
    private String getListenerName(EventListener listener) {
        if (listener instanceof User) {
            return ((User) listener).getName();
        }
        return listener.getClass().getSimpleName();
    }
}