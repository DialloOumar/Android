package com.oumardiallo636.gtuc.troskymate.Entities.Direction;

public class WalkingPoints {

    private String origin;
    private String destination;

    public WalkingPoints(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }
}
