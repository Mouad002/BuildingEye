package com.example.buildingeye.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloController {

    @FXML
    private Button facialRecognitorButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label welcomeText;

    @FXML
    void openFacialRecognitor(ActionEvent event) {

    }

    @FXML
    void openLoginStage(ActionEvent event) {
        URL fxmlLocation = getClass().getResource("/com/example/buildingeye/views/login-view.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("FXML file not found!");
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        try {
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            Stage newWindow = new Stage();
            newWindow.setScene(newScene);
            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
