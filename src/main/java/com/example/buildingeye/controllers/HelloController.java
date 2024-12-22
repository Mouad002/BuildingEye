package com.example.buildingeye.controllers;

import com.example.buildingeye.functional.DatabaseFaceRecognition;
import com.example.buildingeye.functional.FaceRecognitionHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.io.IOException;
import java.net.URL;

public class HelloController {

    @FXML
    private Button facialRecognitorButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label welcomeText;

    private DatabaseFaceRecognition faceRecognition;
    private FaceRecognitionHelper faceRecognitionHelper;
    private VideoCapture webcam;

    public HelloController() {
        this.faceRecognition = new DatabaseFaceRecognition();
        this.faceRecognitionHelper = new FaceRecognitionHelper();
    }

    @FXML
    void openFacialRecognitor(ActionEvent event) {
        // Create a new stage for facial recognition
        Stage recognitionStage = new Stage();
        recognitionStage.setTitle("Facial Recognition");

        // Start webcam in a separate thread
        new Thread(() -> startWebcamCapture()).start();
    }

    private void startWebcamCapture() {
        try {
            webcam = new VideoCapture(0);

            if (webcam == null || !webcam.isOpened()) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Camera Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Could not open the webcam.");
                    alert.showAndWait();
                });
                return;
            }

            Mat frame = new Mat();        // Original color frame
            Mat grayFrame = new Mat();    // Grayscale frame for detection

            String webcamWindow = "Facial Recognition";
            opencv_highgui.namedWindow(webcamWindow);

            while (true) {
                // Read a frame from the webcam
                if (!webcam.read(frame)) {
                    break;
                }

                // Convert to grayscale for face detection
                opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);

                // Detect faces in the grayscale frame
                RectVector faces = new RectVector();
                faceRecognitionHelper.getFaceDetector().detectMultiScale(grayFrame, faces);

                // Process each detected face
                for (int i = 0; i < faces.size(); i++) {
                    Rect faceRect = faces.get(i);

                    // Crop the face from the original color frame
                    Mat croppedFace = new Mat(frame, faceRect);

                    // Perform face recognition
                    DatabaseFaceRecognition.RecognitionResult result = 
                        faceRecognition.recognizeFace(croppedFace);

                    // Draw rectangle and text on the original color frame
                    Scalar color = result.getRectangleColor();
                    opencv_imgproc.rectangle(frame, faceRect, color, 2, 0, 0);
                    
                    // Add text above the rectangle
                    Point textOrigin = new Point(faceRect.x(), faceRect.y() - 10);
                    opencv_imgproc.putText(frame, result.getDisplayText(), 
                        textOrigin, opencv_imgproc.FONT_HERSHEY_SIMPLEX, 
                        0.5, color, 1, opencv_imgproc.LINE_AA, false);
                }

                // Display the frame
                opencv_highgui.imshow(webcamWindow, frame);

                // Wait for key press and check for exit condition
                int key = opencv_highgui.waitKey(30);
                if (key == 'q' || key == 27) { // 'q' or ESC key
                    break;
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Facial Recognition Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred during facial recognition: " + e.getMessage());
                alert.showAndWait();
            });
            e.printStackTrace();
        } finally {
            stopWebcam();
            opencv_highgui.destroyAllWindows();
        }
    }

    private void stopWebcam() {
        if (webcam != null) {
            webcam.release();
        }
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
