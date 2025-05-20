package com.topglobales.comportamientoptrn.patronescomportamiento.command;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.NotificationStrategy;
import com.topglobales.comportamientoptrn.patronescomportamiento.model.User;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Concrete Command: Encapsulates the action of sending a notification.
 */
public class SendNotificationCommand implements Command {
    private final User recipient;
    private final String formattedMessage;
    private final NotificationStrategy strategy;
    private final TextArea logTarget; // For logging the command execution and result

    public SendNotificationCommand(User recipient, String formattedMessage, NotificationStrategy strategy, TextArea logTarget) {
        this.recipient = recipient;
        this.formattedMessage = formattedMessage;
        this.strategy = strategy;
        this.logTarget = logTarget;
    }

    @Override
    public void execute() {
        String commandLog = String.format(">>> Command: Executing SendNotification for User [%s] via %s...",
                recipient.getName(), strategy.toString());
        logToUI(commandLog);

        // The strategy's send method returns the string result.
        String sendResult = strategy.send(recipient, formattedMessage);

        logToUI(sendResult); // Log the result of sending
    }

    private void logToUI(String message) {
        if (logTarget != null) {
            Platform.runLater(() -> logTarget.appendText(message + "\n\n"));
        } else {
            System.out.println(message); // Fallback
        }
    }

    @Override
    public String toString() {
        return String.format("SendNotificationCommand to %s via %s", recipient.getName(), strategy.toString());
    }
}