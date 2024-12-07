package com.example.buildingeye.functional;

import javafx.scene.control.Alert;

public class MyAlert {
    public static void showAlert(String title, String message, Alert.AlertType alertType)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
