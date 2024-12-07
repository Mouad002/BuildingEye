module com.example.buildingeye {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.buildingeye to javafx.fxml;
    exports com.example.buildingeye;
    exports com.example.buildingeye.controllers;
    opens com.example.buildingeye.controllers to javafx.fxml;
}