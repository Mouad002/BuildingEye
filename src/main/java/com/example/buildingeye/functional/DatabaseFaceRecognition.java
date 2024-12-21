package com.example.buildingeye.functional;

import com.example.buildingeye.dao.UserDAO;
import com.example.buildingeye.models.User;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

public class DatabaseFaceRecognition {
    private UserDAO userDAO;
    private FaceRecognitionHelper faceRecognitionHelper;

    public DatabaseFaceRecognition() {
        this.userDAO = new UserDAO();
        this.faceRecognitionHelper = new FaceRecognitionHelper();
    }

    /**
     * Recognizes a face from a cropped face image
     * @param croppedFace Mat representing the cropped face
     * @return RecognitionResult containing authorization status and username
     */
    public RecognitionResult recognizeFace(Mat croppedFace) {
        // Extract embeddings from the detected face
        float[] newEmbeddings = faceRecognitionHelper.faceExtractorFromMat(croppedFace);

        // Fetch all users from the database
        List<User> users = userDAO.getAllUsers();

        // Default to unauthorized
        RecognitionResult result = new RecognitionResult(false, "Access Not Authorized", 
            new Scalar(0, 0, 255, 0)); // Red color

        // Compare with database embeddings
        for (User user : users) {
            // Convert byte[] to float[]
            byte[] embeddingBytes = user.getFaceEmbedding();
            if (embeddingBytes != null) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(embeddingBytes);
                FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
                float[] userEmbeddings = new float[floatBuffer.capacity()];
                floatBuffer.get(userEmbeddings);

                // Calculate cosine similarity
                double distance = faceRecognitionHelper.cosineSimilarity(newEmbeddings, userEmbeddings);

                // If similarity is high enough, mark as authorized
                if (distance > 0.5) {
                    result = new RecognitionResult(true, "Authorized: " + user.getUsername(), 
                        new Scalar(0, 255, 0, 0)); // Green color
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Inner class to encapsulate recognition result
     */
    public static class RecognitionResult {
        private boolean authorized;
        private String displayText;
        private Scalar rectangleColor;

        public RecognitionResult(boolean authorized, String displayText, Scalar rectangleColor) {
            this.authorized = authorized;
            this.displayText = displayText;
            this.rectangleColor = rectangleColor;
        }

        public boolean isAuthorized() {
            return authorized;
        }

        public String getDisplayText() {
            return displayText;
        }

        public Scalar getRectangleColor() {
            return rectangleColor;
        }
    }
}
