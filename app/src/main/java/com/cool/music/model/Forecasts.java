package com.cool.music.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecasts {
    @SerializedName("city")
    private String city;
    @SerializedName("adcode")
    private String adcode;
    @SerializedName("province")
    private String province;
    @SerializedName("reporttime")
    private String reporttime;
    @SerializedName("casts")
    private List<Casts> casts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public List<Casts> getCasts() {
        return casts;
    }

    public void setCasts(List<Casts> casts) {
        this.casts = casts;
    }
}
