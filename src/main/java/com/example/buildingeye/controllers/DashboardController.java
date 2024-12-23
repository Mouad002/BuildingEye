package com.example.buildingeye.controllers;

import com.example.buildingeye.dao.UserDAO;
import com.example.buildingeye.functional.FaceRecognitionHelper;
import com.example.buildingeye.functional.MyAlert;
import com.example.buildingeye.functional.SingletonVideoCapture;
import com.example.buildingeye.models.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.opencv_core.RectVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController {
    @FXML
    private TextField usernameField;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, Void> colActions;
    @FXML
    private Button addButton;

    private FaceRecognitionHelper faceRecognitionHelper;
    private UserDAO userDAO;
    private VideoCapture camera;

    public DashboardController() {
        faceRecognitionHelper = new FaceRecognitionHelper();
        camera = SingletonVideoCapture.getVideoCapture();
        userDAO = new UserDAO();
    }

    @FXML
    public void initialize() {
        // Initialize table columns
        colUsername.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getUsername());
        });
        
        // Configure the actions column with delete button
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                // Style the delete button
                deleteBtn.setStyle("-fx-background-color: #51b84b; -fx-text-fill: white; " +
                                 "-fx-font-size: 12px; -fx-background-radius: 15; " +
                                 "-fx-cursor: hand; -fx-min-width: 60;");

                deleteBtn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        // Load users into table
        refreshTable();
    }

    @FXML
    public void handleLogout() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    private void handleDelete(User user) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION, 
            "Are you sure you want to delete user: " + user.getUsername() + "?",
            ButtonType.YES, ButtonType.NO);
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (userDAO.deleteUser(user.getId())) {
                    refreshTable();
                    MyAlert.showAlert("Success", "User deleted successfully!", Alert.AlertType.INFORMATION);
                } else {
                    MyAlert.showAlert("Error", "Failed to delete user!", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == addButton) {
            addUser();
        }
    }

    private void refreshTable() {
        // Get all users from database and display them in the table
        List<User> users = userDAO.getAllUsers();
        System.out.println("Found " + users.size() + " users:");
        for (User user : users) {
            System.out.println("User: " + user.getUsername());
        }
        usersTable.getItems().clear();
        usersTable.getItems().addAll(users);
    }

    private void addUser() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            MyAlert.showAlert("Error", "Username cannot be empty!", Alert.AlertType.ERROR);
            return;
        }

        // Step 1: check if the camera is opened
        if (!camera.isOpened()) {
            MyAlert.showAlert("Error", "Failed to open the camera!", Alert.AlertType.ERROR);
            return;
        }

        CascadeClassifier faceDetector = faceRecognitionHelper.getFaceDetector();
        List<float[]> embeddingsList = new ArrayList<>();
        int capturedImages = 0;

        while (capturedImages < 5) {
            Mat frame = new Mat();
            if (camera.read(frame)) {
                // Detect face and crop it
                Mat face = detectAndCropFace(frame, faceDetector);
                if (face != null) {
                    // Extract embeddings from the cropped face
                    float[] embeddings = faceRecognitionHelper.faceExtractorFromMat(face);
                    embeddingsList.add(embeddings);
                    capturedImages++;
                    System.out.println("Captured image " + capturedImages + " embeddings" + Arrays.toString(embeddings));
                }

                // Draw rectangle around detected face
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(frame, faces);
                for (int i = 0; i < faces.size(); i++) {
                    Rect faceRect = faces.get(i);
                    opencv_imgproc.rectangle(frame, faceRect, new org.bytedeco.opencv.opencv_core.Scalar(0, 255, 0, 0));
                }
            }

            String webcamWindow = "Webcam - Face Detection";
            opencv_highgui.imshow(webcamWindow, frame);

            if (opencv_highgui.waitKey(30) == 'q') {
                break;
            }
        }

//        camera.release();
        opencv_highgui.destroyAllWindows();

        if (embeddingsList.isEmpty()) {
            MyAlert.showAlert("Error", "No faces were detected!", Alert.AlertType.ERROR);
            return;
        }

        // Convert float array to byte array
        byte[] embeddingBytes = new byte[embeddingsList.get(0).length * 4];
        float[] firstEmbedding = embeddingsList.get(0);
        for (int i = 0; i < firstEmbedding.length; i++) {
            int bits = Float.floatToIntBits(firstEmbedding[i]);
            embeddingBytes[i * 4] = (byte) (bits >> 24);
            embeddingBytes[i * 4 + 1] = (byte) (bits >> 16);
            embeddingBytes[i * 4 + 2] = (byte) (bits >> 8);
            embeddingBytes[i * 4 + 3] = (byte) bits;
        }

        // Create and save new user
        User newUser = new User(username, embeddingBytes);
        if (userDAO.addUser(newUser)) {
            refreshTable();
            MyAlert.showAlert("Success", "User added successfully!", Alert.AlertType.INFORMATION);
        } else {
            MyAlert.showAlert("Error", "Failed to add user!", Alert.AlertType.ERROR);
        }

        usernameField.clear();
    }

    private Mat detectAndCropFace(Mat frame, CascadeClassifier faceDetector) {
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(frame, faces);

        if (faces.empty()) {
            return null;
        }

        Rect faceRect = faces.get(0);
        Mat face = new Mat(frame, faceRect);
        return face;
    }
}
