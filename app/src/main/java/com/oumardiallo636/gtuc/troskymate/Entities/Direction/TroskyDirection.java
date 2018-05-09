
package com.oumardiallo636.gtuc.troskymate.Entities.Direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroskyDirection {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("routes")
    @Expose
    private List<List<Route>> routes = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("stops")
    @Expose
    private List<Stop> stops = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<List<Route>> getRoutes() {
        return routes;
    }

    public void setRoutes(List<List<Route>> routes) {
        this.routes = routes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

}
