package com.topglobales.comportamientoptrn.patronescomportamiento.validator;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NotificationContext {
    private final String eventType;
    private final String originalMessage;
    private boolean isValid = true;
    private final List<String> validationMessages = new ArrayList<>();
    private final TextArea logTarget;


    public NotificationContext(String eventType, String originalMessage, TextArea logTarget) {
        this.eventType = eventType;
        this.originalMessage = originalMessage;
        this.logTarget = logTarget;
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

    public void setIsValid(Boolean data) {
        this.isValid = data;
    }

}