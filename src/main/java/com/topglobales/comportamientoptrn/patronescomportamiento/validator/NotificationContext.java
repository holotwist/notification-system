package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.User;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationContext encapsulates information about an event that requires validation or notification.
 */
@Getter
public class NotificationContext {

    private final String eventType;

    private final String originalMessage;

    private boolean isValid = true;

    private final List<String> validationMessages = new ArrayList<>();

    private final TextArea logTarget;

    private final User recipientUser;

    /**
     * Constructs a new NotificationContext with the given metadata.
     *
     * @param eventType        The type of event.
     * @param originalMessage  The initial message or payload.
     * @param logTarget        UI component to display logs, can be null.
     * @param recipientUser    The user affected by this event, can be null.
     */
    public NotificationContext(String eventType, String originalMessage, TextArea logTarget, User recipientUser) {
        this.eventType = eventType;
        this.originalMessage = originalMessage;
        this.logTarget = logTarget;
        this.recipientUser = recipientUser;
    }

    /**
     * Adds a validation message to the context and logs it to the UI or console.
     *
     * @param message The validation message to add.
     */
    public void addValidationMessage(String message) {
        validationMessages.add(message);
        logToUI("Validation: " + message);
    }

    /**
     * Logs a message to the associated TextArea on the JavaFX Application Thread,
     * or prints to the console if the UI component is not available.
     *
     * @param message The message to log.
     */
    private void logToUI(String message) {
        if (logTarget != null) {
            Platform.runLater(() -> logTarget.appendText(message + "\n"));
        } else {
            System.out.println(message); // Fallback logging
        }
    }

    /**
     * Sets the validity state of the context. Typically called by validation handlers.
     *
     * @param isValid true if the context is valid, false otherwise.
     */
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
}
