package com.lge.olaworks.datastruct;

import android.graphics.Rect;

public class Ola_FaceDetectorInfo {
    private final int MAX_FACE_NUM;
    public Rect[] detectedFaces;
    public int numDetectedFaces;

    public Ola_FaceDetectorInfo() {
        this.MAX_FACE_NUM = 5;
        this.numDetectedFaces = 0;
        this.detectedFaces = new Rect[5];
        for (int i = 0; i < 5; i++) {
            this.detectedFaces[i] = new Rect();
            this.detectedFaces[i].setEmpty();
        }
    }

    public void clear() {
        this.numDetectedFaces = 0;
        for (int i = 0; i < 5; i++) {
            this.detectedFaces[i].setEmpty();
        }
    }
}
