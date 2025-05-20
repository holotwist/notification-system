package com.topglobales.comportamientoptrn.patronescomportamiento.command;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
// import java.util.ArrayList;
// import java.util.List;

/**
 * Invoker: Responsible for executing commands.
 * TODO in the future: Can be extended to support command queuing, history, etc.
 * For now, keep it simple
 */
public class NotificationInvoker {
    // private final List<Command> commandQueue = new ArrayList<>(); // For who wants to complete it, here is an example of command queueing
    private final TextArea logTarget; // For logging invoker actions

    public NotificationInvoker(TextArea logTarget) {
        this.logTarget = logTarget;
    }

    /**
     * Receives a command and executes it.
     * @param command The command to be executed.
     */
    public void setCommand(Command command) {
        String invokerLog = String.format("--- Invoker: Received command [%s]. Executing immediately. ---", command.toString());
        logToUI(invokerLog);
        command.execute();
    }

    /* I do not want to add more functions for now, so, I leave here some examples for maybe one who wants
       To complete it.
       Maybe it will be necessary to adapt more code to be compatible with this... */

    /*

    // Example methods for a queuing system
    public void addCommandToQueue(Command command) {
        commandQueue.add(command);
        logToUI("--- Invoker: Added command to queue: " + command.toString() + ". Queue size: " + commandQueue.size() + " ---");
    }

    public void executeQueue() {
        logToUI("--- Invoker: Executing command queue. Size: " + commandQueue.size() + " ---");
        for (Command command : new ArrayList<>(commandQueue)) { // Iterate copy if commands can be re-added or modify queue
            command.execute();
        }
        commandQueue.clear();
        logToUI("--- Invoker: Command queue executed and cleared. ---");
    }
    */

    private void logToUI(String message) {
        if (logTarget != null) {
            // Ensure UI updates are on the JavaFX Application Thread
            Platform.runLater(() -> logTarget.appendText(message + "\n"));
        } else {
            System.out.println(message); // Fallback if logTarget is not set
        }
    }

    @Override
    public String toString() {
        return "NotificationInvoker";
    }
}