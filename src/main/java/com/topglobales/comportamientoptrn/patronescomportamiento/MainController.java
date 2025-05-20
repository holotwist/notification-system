package com.topglobales.comportamientoptrn.patronescomportamiento;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.*;
import com.topglobales.comportamientoptrn.patronescomportamiento.command.NotificationInvoker;
import com.topglobales.comportamientoptrn.patronescomportamiento.validator.BlockedUserValidator;
import com.topglobales.comportamientoptrn.patronescomportamiento.validator.MessageNotEmptyValidator;
import com.topglobales.comportamientoptrn.patronescomportamiento.validator.ProfanityFilterValidator;
import com.topglobales.comportamientoptrn.patronescomportamiento.validator.ValidationHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML private ListView<User> userListView;
    @FXML private Label lblUserName;
    @FXML private Label lblUserEmail;
    @FXML private Label lblUserPhone;
    @FXML private ComboBox<NotificationStrategy> cmbStrategies;
    @FXML private Button btnUpdateStrategy;

    @FXML private TextField txtEventMessage;
    @FXML private Button btnTriggerSecurity;
    @FXML private Button btnTriggerProfile;
    @FXML private Button btnTriggerPromo;
    @FXML private Button btnTriggerSystem;

    @FXML private TextArea txtLogOutput;

    // --- Backend System Components ---
    private EventManager eventManager;
    private NotificationInvoker notificationInvoker;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<NotificationStrategy> strategies = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Backend Logic (This will now populate 'strategies' list too), simplicity.
        setupNotificationSystem();

        // Populate UI Controls
        userListView.setItems(users);
        // Set ComboBox items after strategies list has been populated by setupNotificationSystem
        cmbStrategies.setItems(strategies);

        // Setup Listeners
        userListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> displayUserDetails(newValue)
        );

        // Initial Log Message
        log("Notification System Initialized. Select a user.\n");
    }

    private void setupNotificationSystem() {
        // --- Create Notification Invoker ---
        this.notificationInvoker = new NotificationInvoker(txtLogOutput);
        log("NotificationInvoker initialized.\n");

        // --- Create Validation Chain (Chain of Responsibility) ---
        ValidationHandler validationChain = new MessageNotEmptyValidator();
        validationChain.setNext(new ProfanityFilterValidator())
                .setNext(new BlockedUserValidator()); // Added BlockedUserValidator

        // Pass the head of the chain to EventManager
        eventManager = new EventManager(validationChain);
        log("EventManager initialized with validation chain.\n");


        // --- Create and Populate Strategies first ---
        NotificationStrategy emailStrategy = new EmailNotification();
        NotificationStrategy smsStrategy = new SMSNotification();
        NotificationStrategy pushStrategy = new PushNotification();

        // Add them to the list that the ComboBox will use
        strategies.addAll(emailStrategy, smsStrategy, pushStrategy);

        // --- Now Create Users using the created strategies (Example data, hardcoded) ---
        AdminUser admin = new AdminUser("Alice Admin", "admin@test.com", "555-0101", emailStrategy);
        ClientUser client1 = new ClientUser("Bob Client", "bob@test.com", "555-0102", smsStrategy);
        ClientUser client2 = new ClientUser("Charlie Client", "charlie@test.com", "555-0103", pushStrategy);
        GuestUser guest = new GuestUser("Diana Guest", "guest@test.com", "555-0104", emailStrategy);

        users.addAll(admin, client1, client2, guest);

        // Link users to the log TextArea and set invoker
        for (User user : users) {
            if (txtLogOutput != null) {
                user.setLogTarget(txtLogOutput);
            } else {
                System.err.println("WARN: txtLogOutput is null during user setup!");
            }
            user.setInvoker(this.notificationInvoker); // Set invoker for each user
        }

        // Example: Set a user as blocked for demonstration (Hardcoded)
        client2.setBlocked(true); // Charlie Client is now blocked
        // Log initial status - user.setBlocked() already logs to UI if logTarget is set.

        // Subscribe Users to Events (Example data, hardcoded)
        eventManager.subscribe("securityAlert", admin);
        eventManager.subscribe("systemUpdate", admin);
        eventManager.subscribe("profileUpdate", client1);
        eventManager.subscribe("promotion", client1);
        eventManager.subscribe("profileUpdate", client2); // Charlie (blocked) is subscribed
        eventManager.subscribe("promotion", guest);

        log("Validation chain configured: MessageNotEmptyValidator -> ProfanityFilterValidator -> BlockedUserValidator\n");
        log(String.format("Initial status: User %s is %s.\n", client2.getName(), client2.isBlocked() ? "blocked" : "not blocked"));
    }

    private void displayUserDetails(User user) {
        if (user != null) {
            lblUserName.setText(user.getName());
            lblUserEmail.setText(user.getEmail());
            lblUserPhone.setText(user.getPhoneNumber());
            cmbStrategies.setValue(user.getPreferredStrategy());
            btnUpdateStrategy.setDisable(false);
            // Could add a display for user.isBlocked() here if someone wants...
        } else {
            lblUserName.setText("-");
            lblUserEmail.setText("-");
            lblUserPhone.setText("-");
            cmbStrategies.setValue(null);
            btnUpdateStrategy.setDisable(true);
        }
    }

    @FXML
    void handleUpdateStrategy(ActionEvent event) {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        NotificationStrategy selectedStrategy = cmbStrategies.getValue();

        if (selectedUser != null && selectedStrategy != null) {
            if (selectedUser.getPreferredStrategy() != selectedStrategy) {
                selectedUser.setPreferredStrategy(selectedStrategy);
                // Log already handled by setPreferredStrategy
            } else {
                log(String.format("User %s already prefers %s. No change made.\n", selectedUser.getName(), selectedStrategy));
            }
        } else {
            log("Please select a user and a notification strategy.\n");
        }
    }

    @FXML
    void handleTriggerEvent(ActionEvent event) {
        String message = txtEventMessage.getText();

        String eventType = "";
        Object source = event.getSource();

        if (source == btnTriggerSecurity) eventType = "securityAlert";
        else if (source == btnTriggerProfile) eventType = "profileUpdate";
        else if (source == btnTriggerPromo) eventType = "promotion";
        else if (source == btnTriggerSystem) eventType = "systemUpdate";
        else { log("Error: Unknown event trigger source.\n"); return; }

        // txtLogOutput is passed to EventManager.notify for CoR validators and EventManager itself to log.
        // User objects (Observers) already have their logTarget set.
        // NotificationInvoker also uses this logTarget.
        eventManager.notify(eventType, message, txtLogOutput); // Pass raw message
    }

    private void log(String message) {
        // Ensure Platform.runLater if logging from non-FX thread, but these calls are from FX thread, so, it's preferred FX thread.
        if (txtLogOutput != null) {
            txtLogOutput.appendText(message); // Append directly, expecting message to have its own newlines if needed or be part of a sequence.
            // For standalone logs, add newline if not present.
            // Most logging methods in the system now add their own newlines.
        } else {
            System.out.println("LOG (txtLogOutput is null): " + message); // Fallback
        }
    }
}