
package com.oumardiallo636.gtuc.troskymate.Entities.Direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

    @SerializedName("bus_name")
    @Expose
    private String busName;
    @SerializedName("stop_location")
    @Expose
    private String stopLocation;
    @SerializedName("stop_name")
    @Expose
    private String stopName;

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getStopLocation() {
        return stopLocation;
    }

    public void setStopLocation(String stopLocation) {
        this.stopLocation = stopLocation;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

}
