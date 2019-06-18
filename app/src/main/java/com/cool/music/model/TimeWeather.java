package com.cool.music.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeWeather {
    @SerializedName("status")
    private String status;
    @SerializedName("count")
    private String count;
    @SerializedName("info")
    private String info;
    @SerializedName("infocode")
    private String infocode;
    @SerializedName("forecasts")
    private List<Forecasts> forecasts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public List<Forecasts> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecasts> forecasts) {
        this.forecasts = forecasts;
    }
}
