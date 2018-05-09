
package com.oumardiallo636.gtuc.troskymate.Entities.CloseBusStop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CloseStops {

    @SerializedName("busStop")
    @Expose
    private List<BusStop> busStop = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;

    public List<BusStop> getBusStop() {
        return busStop;
    }

    public void setBusStop(List<BusStop> busStop) {
        this.busStop = busStop;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
