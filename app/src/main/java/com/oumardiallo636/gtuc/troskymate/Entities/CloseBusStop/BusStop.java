
package com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusStop {

    @SerializedName("stop_location")
    @Expose
    private String stopLocation;
    @SerializedName("stop_name")
    @Expose
    private String stopName;

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
