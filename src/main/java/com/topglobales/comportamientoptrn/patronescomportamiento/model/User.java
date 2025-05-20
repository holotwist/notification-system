package com.topglobales.comportamientoptrn.patronescomportamiento.model;

import com.topglobales.comportamientoptrn.patronescomportamiento.command.Command;
import com.topglobales.comportamientoptrn.patronescomportamiento.command.SendNotificationCommand;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Abstract Class: Defines the template method 'formatMessage' and abstract steps.
 * Also acts as a Concrete Observer, implementing EventListener.
 * Holds context for the Strategy pattern (preferred notification channel).
 */
public abstract class User implements EventListener {
    @Getter
    protected String name;
    @Getter
    protected String email;
    @Getter
    protected String phoneNumber;
    // Method to link this User instance to the UI's log TextArea
    @Setter
    private TextArea logTarget;
    // Strategy Pattern: Each user has a preferred notification strategy
    @Getter
    protected NotificationStrategy preferredStrategy;

    public User(String name, String email, String phoneNumber, NotificationStrategy initialStrategy) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null");
        this.preferredStrategy = Objects.requireNonNull(initialStrategy, "initialStrategy cannot be null");
    }

    // Helper to safely append text to the TextArea from any thread (JVM does not like ConcurrentModification at the same time as other threads)
    protected void logToUI(String message) {
        if (logTarget != null) {
            // Updates to UI must happen on the JavaFX Application Thread (if not, the program might collapse, or Unknown Reference happens)
            Platform.runLater(() -> logTarget.appendText(message + "\n\n"));
        } else {
            System.out.println(message); // Fallback if no target is set
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getClass().getSimpleName());
    }

    // --- Template Method Pattern ---

    /**
     * Template Method: Defines the overall structure for formatting a notification message.
     * It calls abstract or hook methods for specific parts.
     * Marked final to prevent subclasses from overriding the structure.
     *
     * @param rawMessage The basic message content.
     * @return A fully formatted message specific to the user type.
     */
    public final String formatMessage(String rawMessage) {
        String header = getHeader();
        String formattedBody = formatBody(rawMessage);
        String footer = getFooter();
        // Basic validation or default values could be added here
        return header + "\n" + formattedBody + "\n" + footer;
    }

    /**
     * Abstract Step: Subclasses must provide their specific message header.
     *
     * @return The header string.
     */
    protected abstract String getHeader();

    /**
     * Hook Method: Subclasses can optionally override how the body is formatted.
     * Provides a default implementation.
     *
     * @param rawMessage The basic message content.
     * @return The formatted body string.
     */
    protected String formatBody(String rawMessage) {
        return "Message: " + rawMessage;
    }

    /**
     * Abstract Step: Subclasses must provide their specific message footer.
     *
     * @return The footer string.
     */
    protected abstract String getFooter();

    // --- Observer Pattern Implementation ---

    /**
     * Concrete Observer Method: Called when an event this user is subscribed to occurs.
     * It formats the message using the Template Method, creates a Command to send it,
     * and then executes the command.
     */
    @Override
    public void update(String eventType, String message) {
        String logPrefix = String.format(">>> User [%s] received event '%s'. Preparing notification command...",
                getName(), eventType);
        // Log directly using logTarget if available, or sysout. No need for this.logToUI here as command will log. Simplicity.
        if (this.logTarget != null) {
            Platform.runLater(() -> this.logTarget.appendText(logPrefix + "\n"));
        } else {
            System.out.println(logPrefix);
        }


        // Format the message based on user type (Template Method)
        String formattedMessage = this.formatMessage(message);

        // --- Command Pattern: Create and execute a command to send the notification ---
        // The SendNotificationCommand will handle logging of its execution and the strategy's send result.
        Command sendNotificationCmd = new SendNotificationCommand(
                this,
                formattedMessage,
                this.preferredStrategy,
                this.logTarget // Pass the logTarget to the command
        );
        sendNotificationCmd.execute();
    }

    /**
     * Allows changing the user's preferred notification channel (Strategy).
     * @param strategy The new NotificationStrategy to use.
     */
    public void setPreferredStrategy(NotificationStrategy strategy) {
        this.preferredStrategy = Objects.requireNonNull(strategy, "strategy cannot be null");
        String changeMsg = String.format("--- User [%s]: Preference changed to %s ---",
                this.getName(), strategy.toString());
        logToUI(changeMsg);
    }

}