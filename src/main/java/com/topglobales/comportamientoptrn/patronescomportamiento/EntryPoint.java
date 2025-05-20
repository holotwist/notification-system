package com.topglobales.comportamientoptrn.patronescomportamiento;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Entry point of the JavaFX application.
 * Loads the main FXML view and starts the GUI.
 */
public class EntryPoint extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main UI from FXML
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-entry.fxml")));
            Scene scene = new Scene(root, 900, 650);

            primaryStage.setTitle("Notification System Demo");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            // Handles errors while loading the FXML file
            System.err.println("Error loading FXML file:");
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Handles missing FXML resource
            System.err.println("Error: Could not find FXML file. Make sure 'main-entry.fxml' is in the correct path.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}
