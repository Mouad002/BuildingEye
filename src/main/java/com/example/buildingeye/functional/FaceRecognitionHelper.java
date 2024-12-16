package com.example.buildingeye.functional;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.core.CvType;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FaceRecognitionHelper {
    private Session session;
    private CascadeClassifier faceDetector;
    public FaceRecognitionHelper() {
        // load the model of extracting the embeddings and initialize the session
        loadRecognitionModel();
        // load the model of face detection
        loadDetectionModel();
    }

    public CascadeClassifier getFaceDetector() {
        return this.faceDetector;
    }

    public void loadRecognitionModel() {
        // Load the FaceNet model (TensorFlow .pb format)
        URL modelUrl = FaceRecognitionHelper.class.getResource("/com/example/buildingeye/models/20180402-114759.pb");

        // Validate the URL
        if (modelUrl == null) {
            System.err.println("Error: facenet file not found!");
            return;
        }

        // Get the file path
        String modelPath = modelUrl.getPath();

        // Remove the leading slash from the path of the model
        if (modelPath.startsWith("/")) {
            modelPath = modelPath.substring(1);
        }

        try {
            Graph graph = new Graph();
            // Load the graph from the .pb file
            byte[] graphBytes = Files.readAllBytes(Paths.get(modelPath));
            graph.importGraphDef(graphBytes);
            session = new Session(graph);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadDetectionModel() {
        // Get the face model url
        URL faceModelUrl = FaceRecognitionHelper.class.getResource("/com/example/buildingeye/models/haarcascade_frontalface_default.xml");

        // Validate the URL
        if (faceModelUrl == null) {
            System.err.println("Error: haarrcascade file not found!");
            return;
        }

        // Get the file path
        String faceModelPath = faceModelUrl.getPath();

        // Remove the leading slash from the path of the model
        if (faceModelPath.startsWith("/")) {
            faceModelPath = faceModelPath.substring(1); // Remove leading slash
        }

        // Load the Haar Cascade for face detection
        faceDetector = new CascadeClassifier(faceModelPath);

        if (faceDetector.empty()) {
            System.err.println("Error loading face detection model!");
        }
    }

    public float[] faceExtractorFromMat(Mat image) {
        // Resize image to 160x160 pixels
        opencv_imgproc.resize(image, image, new org.bytedeco.opencv.opencv_core.Size(160, 160));

        // Convert BGR to RGB
        opencv_imgproc.cvtColor(image, image, opencv_imgproc.COLOR_BGR2RGB);

        // Normalize image to range [-1, 1]
        image.convertTo(image, CvType.CV_32F);  // Convert to float type
        opencv_core.subtract(image, new Mat(image.size(), image.type(), Scalar.all(127.5)), image); // Center to 0
        opencv_core.divide(image, new Mat(image.size(), image.type(), Scalar.all(127.5)), image); // Normalize to [-1, 1]

        // Convert Mat to float array
        float[] imageData = new float[(int)image.total() * image.channels()];
        FloatPointer dataPointer = new FloatPointer(image.data());
        dataPointer.get(imageData, 0, imageData.length);

        // Create input tensor
        Tensor<Float> inputTensor = Tensor.create(new long[]{1, 160, 160, 3}, FloatBuffer.wrap(imageData));  // Create tensor from image data

        // Run inference and fetch embeddings
        Tensor<Float> outputTensor = session.runner()
                .feed("input", inputTensor)
                .feed("phase_train", Tensors.create(false))
                .fetch("embeddings").run().get(0)
                .expect(Float.class);

        // Extract the embeddings
        int embeddingSize = (int) outputTensor.shape()[1]; // Second dimension of the tensor
        float[][] embeddings = new float[1][embeddingSize]; // Create a 2D array to hold the result
        outputTensor.copyTo(embeddings);

        // return the embeddings as a vector of float
        return embeddings[0];
    }

    public double cosineSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embeddings must have the same length!");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            normA += Math.pow(embedding1[i], 2);
            normB += Math.pow(embedding2[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

