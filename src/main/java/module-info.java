module com.example.buildingeye {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.buildingeye to javafx.fxml;
    exports com.example.buildingeye;
}