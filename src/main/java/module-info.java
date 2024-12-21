module com.example.buildingeye {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bytedeco.javacpp;
    requires org.bytedeco.opencv;
    requires java.sql;
    requires libtensorflow;
    requires java.desktop;

    opens com.example.buildingeye to javafx.fxml;
    opens com.example.buildingeye.controllers to javafx.fxml;
    opens com.example.buildingeye.models to javafx.base;
    
    exports com.example.buildingeye;
    exports com.example.buildingeye.controllers;
}