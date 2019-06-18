package com.cool.music.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LiveWeather {
    @SerializedName("status")
    private String status;
    @SerializedName("count")
    private String count;
    @SerializedName("info")
    private String info;
    @SerializedName("infocode")
    private String infocode;
    @SerializedName("lives")
    private List<Lives> lives;

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

    public List<Lives> getLives() {
        return lives;
    }

    public void setLives(List<Lives> lives) {
        this.lives = lives;
    }
}
