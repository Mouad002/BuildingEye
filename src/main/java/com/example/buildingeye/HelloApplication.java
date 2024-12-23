package com.example.buildingeye;

import com.example.buildingeye.functional.SingletonVideoCapture;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/buildingeye/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("BuildingEye");
        stage.setScene(scene);
        stage.show();
        SingletonVideoCapture.getVideoCapture();
    }

    public static void main(String[] args) {
        launch();

    }
}