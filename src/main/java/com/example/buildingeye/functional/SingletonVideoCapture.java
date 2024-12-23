package com.example.buildingeye.functional;

import org.bytedeco.opencv.opencv_videoio.VideoCapture;

public class SingletonVideoCapture {
    private static VideoCapture videoCapture;


    public static VideoCapture getVideoCapture() {
        if(videoCapture==null) {
            videoCapture = new VideoCapture(0);
        }
        return videoCapture;
    }

}
