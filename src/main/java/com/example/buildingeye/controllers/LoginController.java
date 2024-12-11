package com.example.buildingeye.controllers;

import com.example.buildingeye.dao.AdminDAO;
import com.example.buildingeye.functional.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private Button clearButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField usernameTextField;

    private final AdminDAO adminDAO = new AdminDAO();

    @FXML
    void onClearClick(ActionEvent event) {
        usernameTextField.clear();
        passwordTextField.clear();
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            MyAlert.showAlert("Error", "Filling the text field is necessary", Alert.AlertType.ERROR);
            return;
        }

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (adminDAO.isAdminExists(username, password)) {
            try {
                URL fxmlLocation = getClass().getResource("/com/example/buildingeye/views/dashboard-view.fxml");
                if (fxmlLocation == null) {
                    throw new IllegalStateException("FXML file not found!");
                }
                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                Parent root = loader.load();
                Scene newScene = new Scene(root);
                Stage newWindow = new Stage();
                newWindow.setScene(newScene);
                newWindow.show();
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            MyAlert.showAlert("Error", "Wrong username or password", Alert.AlertType.ERROR);
            usernameTextField.clear();
            passwordTextField.clear();
        }
    }
}
