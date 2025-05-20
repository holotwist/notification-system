package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.User;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NotificationContext {
    private final String eventType;
    private final String originalMessage;
    private boolean isValid = true; // Default to true
    private final List<String> validationMessages = new ArrayList<>();
    private final TextArea logTarget;
    private final User recipientUser; // Can be null if context is not user-specific

    public NotificationContext(String eventType, String originalMessage, TextArea logTarget, User recipientUser) {
        this.eventType = eventType;
        this.originalMessage = originalMessage;
        this.logTarget = logTarget;
        this.recipientUser = recipientUser;
    }

    public void addValidationMessage(String message) {
        validationMessages.add(message);
        logToUI("Validation: " + message);
    }

    private void logToUI(String message) {
        if (logTarget != null) {
            Platform.runLater(() -> logTarget.appendText(message + "\n"));
        } else {
            System.out.println(message); // Fallback
        }
    }

    // Setter for isValid, typically called by validators when a check fails
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

}