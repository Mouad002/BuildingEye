package com.example.buildingeye.controllers;

import com.example.buildingeye.functional.MyAlert;
import com.example.buildingeye.functional.SingletonConnection;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private Button clearButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void onClearClick(ActionEvent event) {
        usernameTextField.clear();
        passwordTextField.clear();
    }

    @FXML
    void onLoginClick(ActionEvent event) {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            MyAlert.showAlert("Error", "Filling the text field is necessary", Alert.AlertType.ERROR);
            return;
        }
        Connection con = SingletonConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("select * from admins where username like ? and password like ?");
            ps.setString(1,usernameTextField.getText());
            ps.setString(2,passwordTextField.getText());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
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
                Node source = (Node)event.getSource();
                Stage stage  = (Stage)source.getScene().getWindow();
                stage.close();
            } else {
                MyAlert.showAlert("Error", "Wrong username or password", Alert.AlertType.ERROR);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
