package com.topglobales.comportamientoptrn.patronescomportamiento;

import com.topglobales.comportamientoptrn.patronescomportamiento.model.*;
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
        // --- Create Validation Chain (Chain of Responsibility) ---
        ValidationHandler validationChain = new MessageNotEmptyValidator();
        validationChain.setNext(new ProfanityFilterValidator());
        // Here goes more validations, maybe future

        // Pass the head of the chain to EventManager
        eventManager = new EventManager(validationChain);

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

        // Link users to the log TextArea
        for (User user : users) {
            if (txtLogOutput != null) {
                user.setLogTarget(txtLogOutput);
            } else {
                System.err.println("WARN: txtLogOutput is null during user setup!");
            }
        }

        // Subscribe Users to Events (Example data, hardcoded)
        eventManager.subscribe("securityAlert", admin);
        eventManager.subscribe("systemUpdate", admin);
        eventManager.subscribe("profileUpdate", client1);
        eventManager.subscribe("promotion", client1);
        eventManager.subscribe("profileUpdate", client2);
        eventManager.subscribe("promotion", guest);

        log("Validation chain configured: MessageNotEmptyValidator -> ProfanityFilterValidator\n");
    }

    private void displayUserDetails(User user) {
        if (user != null) {
            lblUserName.setText(user.getName());
            lblUserEmail.setText(user.getEmail());
            lblUserPhone.setText(user.getPhoneNumber());
            cmbStrategies.setValue(user.getPreferredStrategy());
            btnUpdateStrategy.setDisable(false);
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
        eventManager.notify(eventType, message, txtLogOutput); // Pass raw message
    }

    private void log(String message) {
        // Ensure Platform.runLater if logging from non-FX thread, but these calls are from FX thread, so, it's preferred FX thread.
        if (txtLogOutput != null) {
            txtLogOutput.appendText(message + "\n"); // Add only one newline, others will add their own.
        } else {
            System.out.println("LOG (txtLogOutput is null): " + message); // Fallback
        }
    }
}