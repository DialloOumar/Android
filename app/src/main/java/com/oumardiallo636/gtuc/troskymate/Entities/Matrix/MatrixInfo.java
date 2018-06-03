package com.oumardiallo636.gtuc.troskymate.Entities.Matrix;

public class MatrixInfo {

    int seconds;
    int distance;

    public MatrixInfo(Integer seconds, Integer distance) {
        this.seconds = seconds;
        this.distance = distance;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getDistance() {
        return distance;
    }
}
